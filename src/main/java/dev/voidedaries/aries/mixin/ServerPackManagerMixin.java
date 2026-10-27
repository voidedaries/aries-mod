package dev.voidedaries.aries.mixin;

import com.google.common.hash.HashCode;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.hypixel.ResourcePackServerState;
import dev.voidedaries.aries.client.resourcepack.PersistentHypixelPack;
import dev.voidedaries.aries.client.resourcepack.ServerResourcePackCache;
import net.minecraft.client.resources.server.PackLoadFeedback;
import net.minecraft.client.resources.server.ServerPackManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPackManager.class)
public abstract class ServerPackManagerMixin {

    @Shadow @Final
    private PackLoadFeedback packLoadFeedback;

    @Shadow
    private ServerPackManager.PackPromptStatus packPromptStatus;

    /**
     * Cache verified downloads after Minecraft records their file paths.
     */
    @Inject(method = "onDownload", at = @At("RETURN"))
    private void aries$cacheDownloadedPacks(CallbackInfo ci) {
        if (!AriesFeatures.PERSISTENT_SERVER_RESOURCE_PACK.isEnabled()) {
            return;
        }

        if (!ResourcePackServerState.isHypixel()) {
            return;
        }

        List<?> packs = ((ServerPackManagerAccessor) this).aries$getPacks();

        for (Object entry : packs) {
            ServerPackDataAccessor pack = (ServerPackDataAccessor) entry;

            UUID id = pack.aries$getId();
            HashCode hash = pack.aries$getHash();
            Path path = pack.aries$getPath();

            if (id == null || hash == null || path == null) {
                continue;
            }

            ServerResourcePackCache.cacheDownloaded(id, hash, path);
        }
    }

    @Inject(method = "pushPack", at = @At("HEAD"), cancellable = true)
    private void aries$reuseActiveHypixelPack(UUID id, URL url, HashCode hash, CallbackInfo ci) {
        if (!AriesFeatures.PERSISTENT_SERVER_RESOURCE_PACK.isEnabled()) {
            return;
        }

        if (!ResourcePackServerState.isHypixel()) {
            return;
        }

        if (packPromptStatus != ServerPackManager.PackPromptStatus.ALLOWED) {
            return;
        }

        if (!PersistentHypixelPack.isSameAsActive(id, hash)) {
            return;
        }

        Aries.log("Reusing active Hypixel resource pack {} ({})", id, hash);

        packLoadFeedback.reportUpdate(id, PackLoadFeedback.Update.ACCEPTED);
        packLoadFeedback.reportUpdate(id, PackLoadFeedback.Update.DOWNLOADED);

        packLoadFeedback.reportFinalResult(id, PackLoadFeedback.FinalResult.APPLIED);

        ci.cancel();
    }

}

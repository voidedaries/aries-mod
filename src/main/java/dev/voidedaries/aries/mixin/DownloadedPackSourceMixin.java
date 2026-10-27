
package dev.voidedaries.aries.mixin;

import com.google.common.hash.HashCode;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.hypixel.ResourcePackServerState;
import dev.voidedaries.aries.client.resourcepack.PersistentHypixelPack;
import dev.voidedaries.aries.client.resourcepack.ServerResourcePackCache;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.resources.server.PackReloadConfig;
import net.minecraft.client.resources.server.ServerPackManager;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Mixin(DownloadedPackSource.class)
public abstract class DownloadedPackSourceMixin {

    @Shadow
    @Final
    private ServerPackManager manager;

    @Shadow
    private PackReloadConfig.@Nullable Callbacks pendingReload;

    @Unique
    private PackReloadConfig.@Nullable Callbacks aries$pendingCallbacks;

    @Unique
    private boolean aries$completeThisReload;
    @Unique
    private boolean aries$persistAfterSuccess;

    @Inject(method = "startReload", at = @At("HEAD"))
    private void aries$preparePersistentPack(
        PackReloadConfig.Callbacks callbacks,
        CallbackInfo ci
    ) {
        aries$pendingCallbacks = callbacks;
        aries$completeThisReload = false;
        aries$persistAfterSuccess = false;

        if (!ResourcePackServerState.isHypixel()) {
            PersistentHypixelPack.clearPendingPack();
            return;
        }

        List<PackReloadConfig.IdAndPath> requestedPacks = callbacks.packsToLoad();

        List<?> managedPacks = ((ServerPackManagerAccessor) manager).aries$getPacks();

        PersistentHypixelPack.Entry candidate = null;

        for (PackReloadConfig.IdAndPath requested : requestedPacks) {
            UUID requestedId = requested.id();
            Path requestedPath = requested.path();

            for (Object object : managedPacks) {
                ServerPackDataAccessor pack = (ServerPackDataAccessor) object;

                if (!requestedId.equals(pack.aries$getId()) || !requestedPath.equals(pack.aries$getPath())) {
                    continue;
                }

                HashCode hash = pack.aries$getHash();

                if (hash == null || !ServerResourcePackCache.isCached(requestedId, hash)) {
                    continue;
                }

                Path cachedPath = ServerResourcePackCache.getPackPath(requestedId, hash);

                if (cachedPath != null) {
                    candidate = new PersistentHypixelPack.Entry(requestedId, hash, cachedPath);
                }
            }
        }

        if (candidate == null) {
            PersistentHypixelPack.clearPendingPack();
            return;
        }

        PersistentHypixelPack.setPendingPack(candidate.id(), candidate.hash(), candidate.path());

        Aries.log("Preparing persistent Hypixel pack {} ({})", candidate.id(), candidate.hash());
    }

    @Inject(method = "onReloadSuccess", at = @At("HEAD"))
    private void aries$checkSuccessfulReload(CallbackInfo ci) {
        aries$completeThisReload = pendingReload != null && pendingReload == aries$pendingCallbacks;

        aries$persistAfterSuccess = aries$completeThisReload && PersistentHypixelPack.wasPendingPackOffered();
    }

    @Inject(method = "onReloadSuccess", at = @At("RETURN"))
    private void aries$finishSuccessfulReload(CallbackInfo ci) {
        if (!aries$completeThisReload) {
            return;
        }

        if (aries$persistAfterSuccess) {
            PersistentHypixelPack.Entry pending =
                PersistentHypixelPack.getPendingPack();

            if (pending != null) {
                PersistentHypixelPack.markActive(pending.id(), pending.hash());

                Aries.log("Persisted successfully activated Hypixel pack {} ({})", pending.id(), pending.hash());
            }
        }

        PersistentHypixelPack.clearPendingPack();
        aries$pendingCallbacks = null;
        aries$completeThisReload = false;
        aries$persistAfterSuccess = false;
    }

    @Inject(method = "onRecovery", at = @At("HEAD"))
    private void aries$prepareRecovery(CallbackInfo ci) {
        if (pendingReload != null && pendingReload == aries$pendingCallbacks) {
            PersistentHypixelPack.clearPendingPack();
            aries$pendingCallbacks = null;
            aries$completeThisReload = false;
            aries$persistAfterSuccess = false;
        }
    }

    @Inject(method = "onRecoveryFailure", at = @At("HEAD"))
    private void aries$discardFailedReload(CallbackInfo ci) {
        if (pendingReload != null
            && pendingReload == aries$pendingCallbacks) {
            PersistentHypixelPack.clearPendingPack();
            aries$pendingCallbacks = null;
            aries$completeThisReload = false;
            aries$persistAfterSuccess = false;
        }
    }

    @Inject(method = "startReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/server/PackReloadConfig$Callbacks;onFailure(Z)V"))
    private void aries$clearCandidateBeforeFallback(PackReloadConfig.Callbacks callbacks, CallbackInfo ci) {
        if (callbacks == aries$pendingCallbacks) {
            PersistentHypixelPack.clearPendingPack();
            aries$completeThisReload = false;
            aries$persistAfterSuccess = false;
        }
    }
}

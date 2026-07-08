package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.render.item.EtherwarpRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "submitBlockOutline", at = @At("HEAD"), cancellable = true)
    private void aries$hideVanillaOutline(
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        LevelRenderState levelRenderState,
        CallbackInfo ci
    ) {
        if (EtherwarpRenderer.isActive()) {
            ci.cancel();
        }
    }

}

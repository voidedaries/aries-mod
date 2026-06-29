package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.render.item.EtherwarpRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void hideVanillaOutline(
        MultiBufferSource.BufferSource bufferSource,
        PoseStack poseStack,
        boolean bl,
        LevelRenderState levelRenderState,
        CallbackInfo ci
    ) {
        if (EtherwarpRenderer.isActive()) {
            ci.cancel();
        }
    }

}

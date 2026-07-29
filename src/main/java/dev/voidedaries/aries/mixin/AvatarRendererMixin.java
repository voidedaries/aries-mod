package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.voidedaries.aries.client.command.ClientCommandHooks;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("TAIL"))
    private void aries$spin(
        LivingEntityRenderState state,
        PoseStack poseStack,
        float bodyRot,
        float entityScale,
        CallbackInfo ci
    ) {
        if (!ClientCommandHooks.isSpinning()) {
            return;
        }

        poseStack.mulPose(
            Axis.YP.rotationDegrees(
                (System.currentTimeMillis() % 3600) / 0.5f
            )
        );
    }

    @Inject(method = "scale(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("TAIL"))
    private void aries$playerScale(AvatarRenderState state, PoseStack poseStack, CallbackInfo ci) {
        if (!AriesFeatures.PLAYER_CUSTOMISATION.isEnabled()) {
            return;
        }

        float scale = (float) Math.pow(2, AriesFeatures.PLAYER_CUSTOMISATION.scale.get() * 0.25f);

        float height = (float) Math.pow(2, AriesFeatures.PLAYER_CUSTOMISATION.height.get() * 0.25f);

        float width = (float) Math.pow(2, AriesFeatures.PLAYER_CUSTOMISATION.width.get() * 0.25f);
        float depth = (float) Math.pow(2, AriesFeatures.PLAYER_CUSTOMISATION.depth.get() * 0.25f);

        poseStack.scale(scale * width, scale * height, scale * depth);

        poseStack.scale(scale, scale, scale);
    }

}

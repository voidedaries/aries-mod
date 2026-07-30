package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @ModifyVariable(method = "submitFire", at = @At("HEAD"), argsOnly = true, name = "poseStack")
    private static PoseStack aries$lowFire(PoseStack poseStack) {
        Float offset = AriesFeatures.LOW_FIRE.offset.get();

        if (offset != 0.0f) {
            poseStack.translate(0.0, offset, 0.0);
        }

        return poseStack;
    }

}

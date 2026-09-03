package dev.voidedaries.aries.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private int aries$swingTicks;

    @Unique
    private int aries$swingCooldown;

    @Unique
    private float aries$getSwingProgress(float partialTick) {
        float speed = AriesFeatures.HELD_ITEM_CUSTOMISATION.swingSpeed.get();

        int duration = Mth.clamp(
            (int) (6 / (1 + speed * 0.25f)),
            1,
            20
        );

        if (aries$swingTicks <= 0) {
            return 1.0F;
        }

        float progress = (aries$swingTicks - 1.0F + partialTick) / duration;

        return Mth.clamp(progress, 0.0F, 1.0F);
    }

    @ModifyReturnValue(method = "getAttackAnim(F)F", at = @At("RETURN"))
    private float aries$attackAnim(float original, float partialTick) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return original;
        }

        Aries.log(
            "swingTicks={}, attackAnim={}",
            aries$swingTicks,
            aries$getSwingProgress(partialTick)
        );

        return aries$getSwingProgress(partialTick);
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("TAIL"))
    private void aries$onSwing(InteractionHand hand, CallbackInfo ci) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return;
        }

        // Ignore new swings while cooldown is active
        if (aries$swingCooldown > 0) {
            return;
        }

        aries$swingTicks = 1;

        float speed = AriesFeatures.HELD_ITEM_CUSTOMISATION.swingSpeed.get();

        aries$swingCooldown = Mth.clamp(
            (int)(6 / (1 + speed * 0.25f)),
            1,
            20
        );
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void aries$updateCustomSwing(CallbackInfo ci) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return;
        }

        if (aries$swingCooldown > 0) {
            aries$swingCooldown--;
        }

        if (aries$swingTicks > 0) {
            float speed = AriesFeatures.HELD_ITEM_CUSTOMISATION.swingSpeed.get();

            int duration = Mth.clamp(
                (int) (6 / (1 + speed * 0.25f)),
                1,
                20
            );

            aries$swingTicks++;

            if (aries$swingTicks > duration) {
                aries$swingTicks = 0;
            }
        }
    }

}

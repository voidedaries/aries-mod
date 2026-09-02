package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow
    public float attackAnim;

    @Unique
    private int aries$swingTicks;

    @Unique
    private int aries$swingCooldown;

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("TAIL"), cancellable = true)
    private void aries$onSwing(InteractionHand hand, CallbackInfo ci) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return;
        }

        // Ignore new swings while cooldown is active
        if (aries$swingCooldown > 0) {
            ci.cancel();
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

    @Inject(method = "updateSwingTime", at = @At("TAIL"))
    private void aries$updateSwingTime(CallbackInfo ci) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return;
        }

        if (aries$swingCooldown > 0) {
            aries$swingCooldown--;
        }

        float speed = AriesFeatures.HELD_ITEM_CUSTOMISATION.swingSpeed.get();

        int duration = Mth.clamp((int)(6 / (1 + speed * 0.25f)), 1, 20);

        if (aries$swingTicks > duration) {
            aries$swingTicks = 0;
        }

        if (aries$swingTicks == 0) {
            this.attackAnim = 1F;
        } else {
            this.attackAnim = (aries$swingTicks - 1F) / duration;
            aries$swingTicks++;
        }
    }

}

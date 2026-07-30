package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void aries$clickArmorButtons(
        MouseButtonEvent event,
        boolean doubleClick,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!AriesFeatures.HIDE_ARMOR.isEnabled()) {
            return;
        }

        int x = ((AbstractContainerScreenAccessorMixin) this).aries$getLeftPos();
        int y = ((AbstractContainerScreenAccessorMixin) this).aries$getTopPos();

        if (isInside(event.x(), event.y(), x + 17, y + 8)) {
            AriesFeatures.HIDE_ARMOR.toggle(EquipmentSlot.HEAD);
            cir.setReturnValue(true);
            return;
        }

        if (isInside(event.x(), event.y(), x + 17, y + 26)) {
            AriesFeatures.HIDE_ARMOR.toggle(EquipmentSlot.CHEST);
            cir.setReturnValue(true);
            return;
        }

        if (isInside(event.x(), event.y(), x + 17, y + 44)) {
            AriesFeatures.HIDE_ARMOR.toggle(EquipmentSlot.LEGS);
            cir.setReturnValue(true);
            return;
        }

        if (isInside(event.x(), event.y(), x + 17, y + 62)) {
            AriesFeatures.HIDE_ARMOR.toggle(EquipmentSlot.FEET);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean isInside(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x
            && mouseX <= x + 8
            && mouseY >= y
            && mouseY <= y + 8;
    }

}

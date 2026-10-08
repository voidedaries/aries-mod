package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.gui.TooltipScrollState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedSlot()I"), cancellable = true)
    private void aries$disableSlotSelection(CallbackInfo ci) {
        if (AriesFeatures.SCROLL_PEEK_CHAT.isEnabled() && AriesFeatures.PEEK_CHAT.isDown()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "onScroll", at = @At(value = "STORE"), name = "wheel")
    private int aries$scrollChat(int wheel) {
        if (AriesFeatures.SCROLL_PEEK_CHAT.isEnabled() && AriesFeatures.PEEK_CHAT.isDown()) {
            //? if >=26.2 {
            Minecraft.getInstance().gui.hud.getChat().scrollChat(wheel);
             //?}
            //? if <26.2 {
            /*Minecraft.getInstance().gui.getChat().scrollChat(wheel);
            *///?}
        }
        return wheel;
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void aries$scrollTooltip(long handle, double horizontal, double vertical, CallbackInfo ci) {
        if (!AriesFeatures.TOOLTIP_SCROLL.isEnabled()) {
            return;
        }

        if (vertical == 0 || !TooltipScrollState.hasTooltip()) {
            return;
        }

        Window window = Minecraft.getInstance().getWindow();

        boolean ctrlDown = InputConstants.isKeyDown(window, InputConstants.KEY_LCONTROL)
            || InputConstants.isKeyDown(window, InputConstants.KEY_RCONTROL);

        boolean shiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_LSHIFT)
            || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_RSHIFT);

        int amount = (int) (-vertical * 10);

        if (ctrlDown) {
            TooltipScrollState.changeScale((float) (vertical * 0.1));
            return;
        }

        if (shiftDown) {
            TooltipScrollState.scrollHorizontal(amount);
        } else  {
            TooltipScrollState.scrollVertical(amount);
        }
    }

}

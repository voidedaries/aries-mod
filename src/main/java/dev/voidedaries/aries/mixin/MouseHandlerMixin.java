package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
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
            Minecraft.getInstance().gui.hud.getChat().scrollChat(wheel);
        }
        return wheel;
    }

}

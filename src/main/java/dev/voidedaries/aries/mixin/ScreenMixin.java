package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.gui.TooltipScrollState;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "onClose", at = @At("HEAD"))
    private void aries$resetTooltipScroll(CallbackInfo ci) {
        TooltipScrollState.reset();
    }

}

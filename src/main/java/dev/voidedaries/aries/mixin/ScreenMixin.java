package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.gui.TooltipScrollState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At("HEAD"))
    private void aries$beginTooltipFrame(
        GuiGraphicsExtractor graphics,
        int mouseX, int mouseY,
        float partialTick,
        CallbackInfo ci
    ) {
        TooltipScrollState.tickAnimation();
        TooltipScrollState.beginFrame();
    }

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At("TAIL"))
    private void aries$endTooltipFrame(
        GuiGraphicsExtractor graphics,
        int mouseX, int mouseY,
        float partialTick,
        CallbackInfo ci
    ) {
        TooltipScrollState.endFrame();
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void aries$resetTooltipScroll(CallbackInfo ci) {
        TooltipScrollState.reset();
    }

}

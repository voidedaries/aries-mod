package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.gui.TooltipScrollState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @Inject(method = "tooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"))
    private void aries$captureTooltip(
        Font font,
        List<ClientTooltipComponent> lines,
        int xo,
        int yo,
        ClientTooltipPositioner positioner,
        @Nullable Identifier style,
        CallbackInfo ci
    ) {
        TooltipScrollState.setTooltip(lines);
    }

    @Inject(method = "tooltip", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;", shift = At.Shift.AFTER))
    private void aries$applyTooltipScroll(
        Font font,
        List<ClientTooltipComponent> lines,
        int xo,
        int yo,
        ClientTooltipPositioner positioner,
        @Nullable Identifier style,
        CallbackInfo ci
    ) {
        int scrollX = TooltipScrollState.getScrollX();
        int scrollY = TooltipScrollState.getScrollY();
        float scale = TooltipScrollState.getScale();

        this.pose.translate(xo, yo);
        this.pose.scale(scale, scale);
        this.pose.translate(-xo, -yo);

        this.pose.translate(scrollX, scrollY);
    }

}

package dev.voidedaries.aries.client.gui;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public final class TooltipScrollState {

    private static float scale = 1.0f;
    private static int scrollX;
    private static int scrollY;
    private static List<ClientTooltipComponent> currentTooltip;

    private TooltipScrollState() {}

    public static void reset() {
        scale = 1.0f;
        scrollX = 0;
        scrollY = 0;
        currentTooltip = null;
    }

    public static void setTooltip(List<ClientTooltipComponent> tooltip) {
        currentTooltip = tooltip;
    }

    public static boolean hasTooltip() {
        return currentTooltip != null;
    }

    public static float getScale() {
        return scale;
    }

    public static void changeScale(float amount) {
        scale = Math.clamp(scale + amount, 0.1f, Float.POSITIVE_INFINITY);
    }

    public static int getScrollX() {
        return scrollX;
    }

    public static int getScrollY() {
        return scrollY;
    }

    public static void scrollHorizontal(int amount) {
        if (!hasTooltip()) {
            return;
        }

        scrollX += amount;
    }

    public static void scrollVertical(int amount) {
        if (!hasTooltip()) {
            return;
        }

        scrollY += amount;
    }
}
package dev.voidedaries.aries.client.gui;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public final class TooltipScrollState {

    private static float scale = 1.0f;

    private static List<ClientTooltipComponent> currentTooltip;
    private static boolean tooltipSeenThisFrame;

    private static float displayedScrollX;
    private static float displayedScrollY;

    private static int targetScrollX;
    private static int targetScrollY;

    private static final float SMOOTHING = 0.22f;

    private TooltipScrollState() {}

    public static void reset() {
        resetPosition();
        currentTooltip = null;
        tooltipSeenThisFrame = false;
    }

    private static void resetPosition() {
        scale = 1.0f;

        targetScrollX = 0;
        targetScrollY = 0;

        displayedScrollX = 0.0f;
        displayedScrollY = 0.0f;
    }

    public static void beginFrame() {
        tooltipSeenThisFrame = false;
    }

    public static void endFrame() {
        if (!tooltipSeenThisFrame && currentTooltip != null) {
            reset();
        }
    }

    public static void setTooltip(List<ClientTooltipComponent> tooltip) {
        tooltipSeenThisFrame = true;

        if (currentTooltip == null) {
            currentTooltip = List.copyOf(tooltip);
        }
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

    public static float getScrollX() {
        return displayedScrollX;
    }

    public static float getScrollY() {
        return displayedScrollY;
    }

    public static void scrollVertical(int amount) {
        if (hasTooltip()) {
            targetScrollY += amount;
        }
    }

    public static void scrollHorizontal(int amount) {
        if (hasTooltip()) {
            targetScrollX += amount;
        }
    }

    public static void tickAnimation() {
        displayedScrollX += (targetScrollX - displayedScrollX) * SMOOTHING;
        displayedScrollY += (targetScrollY - displayedScrollY) * SMOOTHING;
    }
}
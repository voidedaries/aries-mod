package dev.voidedaries.aries.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ScreenHelper {

    private ScreenHelper() {}

    public static int centreX(int screenWidth, int elementWidth) {
        return (screenWidth - elementWidth) / 2;
    }

    public static int centreY(int screenHeight, int elementHeight) {
        return (screenHeight - elementHeight) / 2;
    }

    public static void enableScissor(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2) {
        graphics.enableScissor(x1, y1, x2, y2);
    }

    public static void disableScissor(GuiGraphicsExtractor graphics) {
        graphics.disableScissor();
    }

    public static boolean isHovered(
        int mouseX, int mouseY,
        int x, int y,
        int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}

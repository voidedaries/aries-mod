package dev.voidedaries.aries.client.gui;

public class ScreenHelper {

    private ScreenHelper() {}

    public static int centreX(int screenWidth, int elementWidth) {
        return (screenWidth - elementWidth) / 2;
    }

    public static int centreY(int screenHeight, int elementHeight) {
        return (screenHeight - elementHeight) / 2;
    }

    public static boolean isHovered(
        int mouseX, int mouseY,
        int x, int y,
        int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}

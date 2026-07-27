package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.client.feature.types.interaction.ColorPickerInteraction;
import dev.voidedaries.aries.client.render.feature.OpenColorPicker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;

public class ScreenHelper {
    public enum TriangleDirection {
        UP, DOWN, LEFT, RIGHT
    }

    private ScreenHelper() {}

    public static int centreX(int screenWidth, int elementWidth) {
        return (screenWidth - elementWidth) / 2;
    }

    public static int centreY(int screenHeight, int elementHeight) {
        return (screenHeight - elementHeight) / 2;
    }

    public static void drawButton(
        GuiGraphicsExtractor graphics,
        Font font,
        int x,
        int y,
        int width,
        int height,
        MutableComponent text,
        boolean hovered
    ) {
        graphics.fill(x, y, x + width, y + height, 0xFF151A21);

        graphics.centeredText(
            font,
            text,
            x + width / 2, y + (height - font.lineHeight) / 2 + 1,
             hovered ? 0xFFFFFFFF : 0xFFADB5C9
        );
    }

    public static boolean isOverButton(int mouseX, int mouseY, int x, int y, int width, int height) {
        return ScreenHelper.isHovered(mouseX, mouseY, x, y, width, height);
    }

    public static int getButtonY(OpenColorPicker picker) {
        int padding = AriesScreen.PADDING / 2;

        int hsvBoxY = ColorPickerInteraction.getColorPickerHSVY(picker);
        int hsvBoxHeight = ColorPickerInteraction.getColorPickerHSVHeight();

        int hueBarY = (int) (hsvBoxY + hsvBoxHeight + (AriesScreen.PADDING / 1.5));
        int alphaBarY = hueBarY + (AriesScreen.PADDING * 2);

        return alphaBarY + (padding * 3);
    }

    public static int getButtonHeight() {
        return 16;
    }

    public static int getButtonGap() {
        return 4;
    }

    public static int getButtonWidth(OpenColorPicker picker) {
        return (ColorPickerInteraction.getColorPickerHSVWidth(picker) - getButtonGap() * 2) / 3;
    }

    public static void drawTriangle(
        GuiGraphicsExtractor graphics,
        int centerX,
        int centerY,
        int size,
        int color,
        TriangleDirection direction
    ) {
        int half = size / 2;

        switch (direction) {
            case UP -> {
                for (int i = 0; i < half; i++) {
                    graphics.fill(
                        centerX - i,
                        centerY + i,
                        centerX + i + 1,
                        centerY + i + 1,
                        color
                    );
                }
            }

            case DOWN -> {
                for (int i = 0; i < half; i++) {
                    graphics.fill(
                        centerX - (half - i - 1),
                        centerY + i,
                        centerX + (half - i),
                        centerY + i + 1,
                        color
                    );
                }
            }

            case LEFT -> {
                for (int i = 0; i < half; i++) {
                    graphics.fill(
                        centerX + i,
                        centerY - i,
                        centerX + i + 1,
                        centerY + i + 1,
                        color
                    );
                }
            }

            case RIGHT -> {
                for (int i = 0; i < half; i++) {
                    graphics.fill(
                        centerX - i,
                        centerY - i,
                        centerX - i + 1,
                        centerY + i + 1,
                        color
                    );
                }
            }
        }
    }

    public static boolean isHovered(
        int mouseX, int mouseY,
        int x, int y,
        int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}

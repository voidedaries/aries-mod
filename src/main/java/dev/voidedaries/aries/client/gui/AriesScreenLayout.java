package dev.voidedaries.aries.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public class AriesScreenLayout {

    private AriesScreenLayout() {}

    public static void drawHSVBox(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        int width,
        int height,
        int hueColor
    ) {
        // Base hue
        graphics.fill(x, y, x + width, y + height, hueColor);

        // White -> transparent horizontally
        for (int i = 0; i < width; i++) {
            float alpha = 1f - (i / (float) width);

            int color = ((int)(alpha * 255) << 24) | 0xFFFFFF;

            graphics.fill(x + i, y, x + i + 1, y + height, color);
        }

        // Transparent -> black vertically
        for (int i = 0; i < height; i++) {
            float alpha = i / (float) height;

            int color = ((int)(alpha * 255) << 24);

            graphics.fill(x, y + i, x + width, y + i + 1, color);
        }
    }

    public static void drawHueBar(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        int[] colors = {
            0xFFFF0000, // red
            0xFFFFFF00, // yellow
            0xFF00FF00, // green
            0xFF00FFFF, // cyan
            0xFF0000FF, // blue
            0xFFFF00FF, // magenta
            0xFFFF0000, // red
        };

        for (int i = 0; i < width; i++) {
            float percent = i / (float) width;

            float scaled = percent * (colors.length - 1);

            int index = (int) scaled;
            float local = scaled - index;

            int color = interpolateColor(colors[index], colors[Math.min(index + 1, colors.length - 1)], local);

            graphics.fill(x + i, y, x + i + 1, y + height, color);
        }
    }

    public static void drawAlphaBar(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        int width,
        int height,
        int color
    ) {
        for (int i = 0; i < width; i++) {
            float alpha = i / (float) width;

            int result =
                ((int)(alpha * 255) << 24)
                    | (color & 0xFFFFFF);

            graphics.fill(
                x + i,
                y,
                x + i + 1,
                y + height,
                result
            );
        }
    }

    private static int interpolateColor(int a, int b, float t) {
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;

        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        int r = (int) (ar + (br - ar) * t);
        int g = (int) (ag + (bg - ag) * t);
        int b2 = (int) (ab + (bb - ab) * t);

        return 0xFF000000 | (r << 16) | (g << 8) | b2;
    }

    public static int calculateThumbHeight(int scrollbarHeight, int contentHeight, int visibleHeight) {
        if (contentHeight <= 0 || contentHeight <= visibleHeight) {
            return scrollbarHeight;
        }

        float visibleRatio = visibleHeight / (float) contentHeight;

        return Math.clamp((int) (scrollbarHeight * visibleRatio), 20, scrollbarHeight);
    }

    //Scrollbar methods
    public static int calculateThumbY(
        int scrollbarTop,
        int scrollbarHeight,
        int thumbHeight,
        int scrollOffset,
        int contentHeight,
        int visibleHeight
    ) {
        int scrollRange = contentHeight - visibleHeight;
        int thumbRange = scrollbarHeight - thumbHeight;

        if (scrollRange <= 0) {
            return scrollbarTop;
        }

        return scrollbarTop +
            (int)((scrollOffset / (float) scrollRange) * thumbRange);
    }

    public static int calculateScrollbarHeight(int visibleHeight, int padding) {
        return visibleHeight - (padding * 2);
    }

    public static int getScrollbarTop(int screenHeight, int menuHeight, int padding, int fontHeight) {
        int menuY = ScreenHelper.centreY(screenHeight, menuHeight);

        return (int) (menuY + padding + fontHeight + padding * 1.5);
    }

    public static int getContentVisibleHeight(
        int screenHeight,
        int menuHeight,
        int padding,
        int fontHeight
    ) {
        int menuY = ScreenHelper.centreY(screenHeight, menuHeight);

        int contentY = (int) (
            menuY
                + padding
                + fontHeight
                + padding * 1.5
        );

        int contentTop = contentY - padding;
        int contentBottom = menuY + menuHeight;

        return contentBottom - contentTop;
    }

    public static int getMaxScroll(int contentHeight, int visibleHeight) {
        return Math.max(0, contentHeight - visibleHeight);
    }

    public static int calculateScrollOffsetFromDrag(
        double mouseY,
        int scrollbarOffset,
        int scrollbarTop,
        int scrollbarHeight,
        int thumbHeight,
        int contentHeight,
        int visibleHeight
    ) {
        int thumbRange = scrollbarHeight - thumbHeight;

        if (thumbRange <= 0) {
            return 0;
        }

        float percent = ((float) mouseY - scrollbarOffset - scrollbarTop) / thumbRange;
        percent = Mth.clamp(percent, 0f, 1f);

        int maxScroll = getMaxScroll(contentHeight, visibleHeight);

        return (int) (percent * maxScroll);
    }

    public static int clampScroll(
        int scrollOffset,
        int contentHeight,
        int visibleHeight
    ) {
        int maxScroll = contentHeight - visibleHeight;

        return Mth.clamp(
            scrollOffset,
            0,
            Math.max(0, maxScroll)
        );
    }

}

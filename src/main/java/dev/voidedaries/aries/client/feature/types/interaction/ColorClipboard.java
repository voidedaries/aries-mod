package dev.voidedaries.aries.client.feature.types.interaction;

public class ColorClipboard {
    private static Integer clipboardColor;

    private ColorClipboard() {}

    public static void copy(int color) {
        clipboardColor = color;
    }

    public static int paste(int fallback) {
        return clipboardColor != null ? clipboardColor : fallback;
    }

    public static boolean hasColor() {
        return clipboardColor != null;
    }

}

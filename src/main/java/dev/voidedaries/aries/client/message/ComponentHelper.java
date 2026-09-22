package dev.voidedaries.aries.client.message;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class ComponentHelper {

    public static MutableComponent gradient(String text, int startColor, int endColor) {
        MutableComponent component = Component.empty();

        for (int i = 0; i < text.length(); i++) {
            float progress = text.length() == 1
                ? 0.0f
                : (float) i / (text.length() - 1);

            int color = interpolateColor(startColor, endColor, progress);

            component.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(Style.EMPTY.withColor(color)));
        }

        return component;
    }

    private static int interpolateColor(int startColor, int endColor, float progress) {
        int startRed = (startColor >> 16) & 0xFF;
        int startGreen = (startColor >> 8) & 0xFF;
        int startBlue = startColor & 0xFF;

        int endRed = (endColor >> 16) & 0xFF;
        int endGreen = (endColor >> 8) & 0xFF;
        int endBlue = endColor & 0xFF;

        int red = (int) (startRed + (endRed - startRed) * progress);
        int green = (int) (startGreen + (endGreen - startGreen) * progress);
        int blue = (int) (startBlue + (endBlue - startBlue) * progress);

        return (red << 16) | (green << 8) | blue;
    }

}

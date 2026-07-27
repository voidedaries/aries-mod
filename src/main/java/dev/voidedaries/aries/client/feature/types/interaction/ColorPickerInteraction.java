package dev.voidedaries.aries.client.feature.types.interaction;

import dev.voidedaries.aries.client.gui.AriesScreen;
import dev.voidedaries.aries.client.render.feature.OpenColorPicker;
import net.minecraft.util.Mth;

public class ColorPickerInteraction {

    public static int getColorPickerBarHeight() {
        return (int) (((double) AriesScreen.PADDING / 2) * 1.75);
    }

    private static int getColorPickerBoxX(OpenColorPicker picker) {
        return picker.getX() - picker.getWidth() / 2;
    }

    public static int getColorPickerHSVX(OpenColorPicker picker) {
        return getColorPickerBoxX(picker) + AriesScreen.PADDING / 2;
    }

    public static int getColorPickerHSVY(OpenColorPicker picker) {
        int boxY = picker.getY() + 6; // triangle height stuff
        int startY = boxY + (AriesScreen.PADDING * 2);

        return (int) (startY - (AriesScreen.PADDING * 1.25));
    }

    public static int getColorPickerHSVWidth(OpenColorPicker picker) {
        return picker.getWidth() - ((AriesScreen.PADDING / 2) * 2);
    }

    public static int getColorPickerHSVHeight() {
        return (int) (AriesScreen.COLOR_PICKER_BOX_HEIGHT / 2.75);
    }

    public static int getColorPickerHueY(OpenColorPicker picker) {
        return (int) (getColorPickerHSVY(picker) + getColorPickerHSVHeight() + (AriesScreen.PADDING / 1.5));
    }

    public static int getColorPickerAlphaY(OpenColorPicker picker) {
        return getColorPickerHueY(picker) + (AriesScreen.PADDING * 2);
    }

    public static void updateHue(ColorPickerState state, int mouseX, int x, int width) {
        float hue = (mouseX - x) / (float) width;

        hue = Mth.clamp(hue, 0f, 1f);

        state.setHue(hue);
    }

    public static void updateAlpha(ColorPickerState state, int mouseX, int x, int width) {
        float alpha = (mouseX - x) / (float) width;

        alpha = Mth.clamp(alpha, 0f, 1f);

        state.setAlpha(alpha);
    }

    public static void updateHSVArea(
        ColorPickerState state,
        int mouseX,
        int mouseY,
        int x,
        int y,
        int width,
        int height
    ) {
        float saturation = (mouseX - x) / (float) width;
        float brightness = 1.0f - ((mouseY - y) / (float) height);

        state.setSaturation(Mth.clamp(saturation, 0.0f, 1.0f));
        state.setBrightness(Mth.clamp(brightness, 0.0f, 1.0f));
    }
}

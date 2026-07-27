package dev.voidedaries.aries.client.feature.types.interaction;

import net.minecraft.util.Mth;

import java.awt.*;

public class ColorPickerState {
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;

    public ColorPickerState(float hue, float saturation, float brightness, float alpha) {
        setHue(hue);
        setSaturation(saturation);
        setBrightness(brightness);
        setAlpha(alpha);
    }

    public static ColorPickerState fromARGB(int color) {
        float[] hsv = new float[3];

        Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsv);

        float alpha = ((color >> 24) & 0xFF) / 255f;

        return new ColorPickerState(hsv[0], hsv[1], hsv[2], alpha);
    }

    public int getARGB() {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);

        return ((int) (alpha * 255) << 24 | (rgb & 0xFFFFFF));
    }

    public void setARGB(int color) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        float[] hsv = Color.RGBtoHSB(red, green, blue, null);
        this.hue = hsv[0];
        this.saturation = hsv[1];
        this.brightness = hsv[2];
        this.alpha = alpha / 255f;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = Mth.clamp(hue, 0.0f, 1.0f);
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = Mth.clamp(saturation, 0.0f, 1.0f);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = Mth.clamp(brightness, 0.0f, 1.0f);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = Mth.clamp(alpha, 0.0f, 1.0f);
    }

}

package dev.voidedaries.aries.client.feature.types;

import net.minecraft.util.Mth;

public class SliderEditState {

    private final SliderValue slider;
    private String input;

    private long lastBlink = System.currentTimeMillis();
    private boolean caretVisible = true;

    public SliderEditState(SliderValue slider) {
        this.slider = slider;
        this.input = formatInput(slider.getAsFloat());
    }

    public SliderValue getSlider() {
        return slider;
    }

    public String getInput() {
        return input;
    }

    private String formatInput(float value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }

        return String.valueOf(value);
    }

    public void addChar(char c) {
        if (c == '-' && !input.isEmpty()) {
            return;
        }

        if (c == '.' && input.contains(".")) {
            return;
        }

        input += c;
        resetBlink();
    }

    public boolean showCaret() {
        if (System.currentTimeMillis() - lastBlink > 500) {
            caretVisible = !caretVisible;
            lastBlink = System.currentTimeMillis();
        }

        return caretVisible;
    }

    private void resetBlink() {
        lastBlink = System.currentTimeMillis();
        caretVisible = true;
    }

    public void backspace() {
        if (!input.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            resetBlink();
        }
    }

    public boolean apply() {
        try {
            float value = Float.parseFloat(input);

            value = Mth.clamp(value, slider.getMin(), slider.getMax());
            float percent = (value - slider.getMin()) / (slider.getMax() - slider.getMin());

            slider.setFromPercent(percent);

            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

}

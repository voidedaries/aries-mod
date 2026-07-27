package dev.voidedaries.aries.client.feature.types.interaction;

import net.minecraft.util.Mth;

public class SliderEditState extends EditState {

    private final SliderValue slider;

    public SliderEditState(SliderValue slider) {
        super(formatInput(slider.getAsFloat()));
        this.slider = slider;
    }

    public SliderValue getSlider() {
        return slider;
    }

    @Override
    public void addChar(char c) {
        if (c == '-' && !input.isEmpty()) {
            return;
        }

        if (c == '.' && input.contains(".")) {
            return;
        }

        super.addChar(c);
    }

    @Override
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

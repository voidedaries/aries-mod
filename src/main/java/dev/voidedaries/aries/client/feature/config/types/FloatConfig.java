package dev.voidedaries.aries.client.feature.config.types;

import dev.voidedaries.aries.client.feature.config.ConfigTypes;

public class FloatConfig extends AriesConfigType<Float> implements SliderConfig {

    private final float min;
    private final float max;

    public FloatConfig(String key, Float defaultValue, float min, float max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public float getMin() {
        return min;
    }

    @Override
    public float getMax() {
        return max;
    }

    @Override
    public float getAsFloat() {
        return get();
    }

    @Override
    public void setFromPercent(float percent) {
        float value = min + percent * (max - min);
        set(value);
    }

    @Override
    protected Float validate(Float value) {
        return Math.clamp(value, min, max);
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.SLIDER;
    }

}

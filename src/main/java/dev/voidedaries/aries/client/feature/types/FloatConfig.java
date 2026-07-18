package dev.voidedaries.aries.client.feature.types;

public class FloatConfig extends AriesConfigType<Float> implements SliderValue {

    private final float min;
    private final float max;
    private final float step;

    public FloatConfig(String key, Float defaultValue, float min, float max, float step) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
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
    public float getStep() {
        return step;
    }

    @Override
    public void setFromPercent(float percent) {
        float value = min + percent * (max - min);

        value = Math.round(value / step) * step;

        setSilently(value);
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

package dev.voidedaries.aries.client.feature.types;

public class IntConfig extends AriesConfigType<Integer> implements SliderValue {

    private final int min;
    private final int max;

    public IntConfig(String key, Integer defaultValue,  int min, int max) {
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
        int value = min + Math.round(percent * (max - min));
        setSilently(value);
    }

    @Override
    protected Integer validate(Integer value) {
        return Math.clamp(value, min, max);
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.SLIDER;
    }

}

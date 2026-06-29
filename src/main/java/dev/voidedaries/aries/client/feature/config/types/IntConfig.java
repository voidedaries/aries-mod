package dev.voidedaries.aries.client.feature.config.types;

import dev.voidedaries.aries.client.feature.config.ConfigTypes;

public class IntConfig extends AriesConfigType<Integer> {

    private final int min;
    private final int max;

    public IntConfig(String key, Integer defaultValue,  int min, int max) {
        super(key, defaultValue);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }
    public int getMax() {
        return max;
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

package dev.voidedaries.aries.client.feature.config;

import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;

public class ColorConfig extends AriesConfigType<Integer> {

    public ColorConfig(String key, int defaultValue) {
        super(key, defaultValue);
    }

    @Override
    protected Integer validate(Integer value) {
        return value == null ? defaultValue : value;
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.COLOR;
    }

    public static int parse(String raw) {
        raw = raw.trim();
        if (raw.startsWith("0x") || raw.startsWith("0X")) {
            raw = raw.substring(2);
        }
        return (int) Long.parseUnsignedLong(raw, 16);
    }
}

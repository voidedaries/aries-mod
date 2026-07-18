package dev.voidedaries.aries.client.feature.config;

import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;

public class BooleanConfig extends AriesConfigType<Boolean> {

    public BooleanConfig(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.TOGGLE;
    }
}

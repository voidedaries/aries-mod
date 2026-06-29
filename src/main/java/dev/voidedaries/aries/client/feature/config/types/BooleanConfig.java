package dev.voidedaries.aries.client.feature.config.types;

import dev.voidedaries.aries.client.feature.config.ConfigTypes;

public class BooleanConfig extends AriesConfigType<Boolean> {

    public BooleanConfig(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.TOGGLE;
    }
}

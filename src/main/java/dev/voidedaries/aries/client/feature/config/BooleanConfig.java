package dev.voidedaries.aries.client.feature.config;

public class BooleanConfig extends AriesConfigType<Boolean> {

    public BooleanConfig(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.TOGGLE;
    }
}

package dev.voidedaries.aries.client.feature.config;

import dev.voidedaries.aries.client.AriesConfig;

public abstract class AriesConfigType<T> {

    private final String key;
    protected final T defaultValue;

    private T value;

    public AriesConfigType(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        this.value = validate(newValue);

        AriesConfig.save();
    }

    protected T validate(T value) {
        return value;
    }

    public String getKey() {
        return key;
    }

    public abstract ConfigTypes getType();

}

package dev.voidedaries.aries.client.feature.config.types;

import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.config.ConfigTypes;

import java.util.function.Supplier;

public abstract class AriesConfigType<T> {

    private final String key;
    protected final T defaultValue;

    private AriesFeature feature;
    private Supplier<Boolean> visibleCondition = () -> true;

    private T value;

    public AriesConfigType(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public boolean isVisible() {
        return visibleCondition.get();
    }

    public AriesConfigType<?> visibleWhen(Supplier<Boolean> condition) {
        this.visibleCondition = condition;
        return this;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        this.value = validate(newValue);

        AriesConfig.save();
    }

    public void setSilently(T newValue) {
        this.value = validate(newValue);
    }

    public void setValue(T newValue) {
        this.value = validate(newValue);
    }

    protected T validate(T value) {
        return value;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public abstract ConfigTypes getType();

    public void setFeature(AriesFeature feature) {
        this.feature = feature;
    }

    public AriesFeature getFeature() {
        return feature;
    }

}

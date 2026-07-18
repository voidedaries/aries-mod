package dev.voidedaries.aries.client.feature.types;

import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.AriesFeature;

import java.util.function.Supplier;

/**
 * Base class for all Aries configuration values.
 *
 * <p>A config type stores its key, default value, current value,
 * owning feature, and visibility conditions. Specific config types
 * extend this class to provide specialized behavior such as toggles,
 * sliders, colors, and keybinds.</p>
 *
 * @param <T> the value type stored by this configuration
 */
public abstract class AriesConfigType<T> {

    private final String key;
    protected final T defaultValue;

    private AriesFeature feature;
    private Supplier<Boolean> visibleCondition = () -> true;

    private T value;

    /**
     * Creates a new configuration value.
     *
     * @param key unique identifier used when saving/loading
     * @param defaultValue initial value and reset value
     */
    public AriesConfigType(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    /**
     * Checks whether this config should currently be displayed.
     *
     * @return true if the config is visible
     */
    public boolean isVisible() {
        return visibleCondition.get();
    }

    /**
     * Controls when this config is shown in the menu.
     *
     * @param condition visibility condition
     * @return this config instance for chaining
     */
    public AriesConfigType<?> visibleWhen(Supplier<Boolean> condition) {
        this.visibleCondition = condition;
        return this;
    }

    /**
     * Gets the current stored value.
     *
     * @return current config value
     */
    public T get() {
        return value;
    }

    /**
     * Updates the config value and saves the configuration.
     *
     * @param newValue new value to apply
     */
    public void set(T newValue) {
        this.value = validate(newValue);

        AriesConfig.save();
    }

    /**
     * Updates the config value without saving.
     *
     * <p>Useful when loading configuration data.</p>
     *
     * @param newValue new value to apply
     */
    public void setSilently(T newValue) {
        this.value = validate(newValue);
    }

    /**
     * Updates the config value without triggering save logic.
     *
     * @param newValue new value to apply
     */
    public void setValue(T newValue) {
        this.value = validate(newValue);
    }

    /**
     * Allows subclasses to restrict or modify values before storage.
     *
     * @param value value being validated
     * @return validated value
     */
    protected T validate(T value) {
        return value;
    }

    /**
     * @return unique configuration key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return default configuration value
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return config type used by the renderer
     */
    public abstract ConfigTypes getType();

    /**
     * Assigns the feature that owns this configuration.
     *
     * @param feature owning Aries feature
     */
    public void setFeature(AriesFeature feature) {
        this.feature = feature;
    }

    /**
     * @return feature that owns this configuration
     */
    public AriesFeature getFeature() {
        return feature;
    }

}

package dev.voidedaries.aries.client.feature.types;

import java.util.Arrays;
import java.util.List;

public class ListConfig<T> extends AriesConfigType<T> {
    private final List<T> values;

    @SafeVarargs
    public ListConfig(String key, T defaultValue, T... values) {
        super(key, defaultValue);
        this.values = List.copyOf(Arrays.asList(values));

        if (!this.values.contains(defaultValue)) {
            throw new IllegalArgumentException("Default value " + defaultValue + " is not present in ListConfig values");
        }
    }

    public List<T> getValues() {
        return values;
    }

    public String getValueName() {
        return String.valueOf(get());
    }

    public void setIndex(int index) {
        if (index < 0 || index >= values.size()) {
            return;
        }

        set(values.get(index));
    }

    public int getIndex() {
        return values.indexOf(get());
    }

    public void next() {
        int index = getIndex();

        if (index == -1 || index >= values.size() - 1) {
            setIndex(0);
            return;
        }

        setIndex(index + 1);
    }

    public void previous() {
        int index = getIndex();

        if (index <= 0) {
            setIndex(values.size() - 1);
            return;
        }

        setIndex(index - 1);
    }

    public String serialize() {
        Object value = get();

        if (value instanceof Enum<?> enumValue) {
            return enumValue.name();
        }

        return value.toString();
    }

    public void deserialize(String value) {
        if (get() instanceof Enum<?>) {
            values.stream()
                .filter(option -> option instanceof Enum<?> enumOption && enumOption.name().equals(value))
                .findFirst()
                .ifPresent(this::set);
        }
    }

    @Override
    protected T validate(T value) {
        if (value == null || !values.contains(value)) {
            return defaultValue;
        }

        return value;
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.LIST;
    }
}

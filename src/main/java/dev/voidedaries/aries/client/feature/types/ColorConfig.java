package dev.voidedaries.aries.client.feature.types;

public class ColorConfig extends AriesConfigType<Integer> {

    public ColorConfig(String key, int value) {
        super(key, value);
    }

    public static String formatColor(int color) {
        return String.format("0x%08X", color);
    }

    public void reset() {
        set(getDefaultValue());
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

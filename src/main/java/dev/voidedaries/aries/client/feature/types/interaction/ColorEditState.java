package dev.voidedaries.aries.client.feature.types.interaction;

import dev.voidedaries.aries.client.feature.types.ColorConfig;

public class ColorEditState extends EditState {

    private final ColorConfig color;

    public ColorEditState(ColorConfig color) {
        super(ColorConfig.formatColor(color.get()).replace("0x", ""));
        this.color = color;
    }

    public ColorConfig getColor() {
        return color;
    }

    public void updatePreview() {

        StringBuilder padded = new StringBuilder(input);

        while (padded.length() < 8) {
            padded.append("0");
        }

        try {
            int value = (int) Long.parseLong(padded.toString(), 16);
            color.set(value);

        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void addChar(char c) {
        if (!Character.isDigit(c) && !(c >= 'A' && c <= 'F') && !(c >= 'a' && c <= 'f')) {
            return;
        }

        if (input.length() >= 8) {
            return;
        }
        super.addChar(Character.toUpperCase(c));
        updatePreview();
    }

    @Override
    public boolean apply() {
        if (input.length() != 8) {
            return false;
        }

        try {
            int value = (int) Long.parseLong(input, 16);

            color.set(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public void backspace() {
        super.backspace();
        updatePreview();
    }
}

package dev.voidedaries.aries.client.render.feature;

import dev.voidedaries.aries.client.feature.types.ColorConfig;
import dev.voidedaries.aries.client.feature.types.interaction.ColorPickerState;

public final class OpenColorPicker {
    private final ColorConfig config;

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final ColorPickerState state;

    public OpenColorPicker(ColorConfig config, int x, int y, int width, int height) {
        this.config = config;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.state = ColorPickerState.fromARGB(config.get());
    }

    public ColorConfig getConfig() {
        return config;
    }

    public ColorPickerState getState() {
        return state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}

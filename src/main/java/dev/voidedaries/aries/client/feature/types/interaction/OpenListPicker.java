package dev.voidedaries.aries.client.feature.types.interaction;

import dev.voidedaries.aries.client.feature.types.ListConfig;

@SuppressWarnings("ClassCanBeRecord")
public class OpenListPicker {

    private final ListConfig<?> config;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public OpenListPicker(ListConfig<?> config, int x, int y, int width, int height) {
        this.config = config;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ListConfig<?> getConfig() {
        return config;
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

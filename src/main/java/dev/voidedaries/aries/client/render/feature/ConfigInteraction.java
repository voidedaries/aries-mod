package dev.voidedaries.aries.client.render.feature;

import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.function.Consumer;

public record ConfigInteraction(
    AriesConfigType<?> config,
    int x,
    int y,
    int width,
    int height,
    Consumer<MouseButtonEvent> click
) {

    public ConfigInteraction(AriesConfigType<?> config, int x, int y, int width, int height) {
        this(config, x, y, width, height, null);
    }

}
package dev.voidedaries.aries.client.feature.config.render;

import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;

public record ConfigInteraction(AriesConfigType<?> config, int x, int y, int width, int height) {}
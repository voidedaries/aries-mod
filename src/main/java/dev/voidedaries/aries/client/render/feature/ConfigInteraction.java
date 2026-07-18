package dev.voidedaries.aries.client.render.feature;

import dev.voidedaries.aries.client.feature.types.AriesConfigType;

public record ConfigInteraction(AriesConfigType<?> config, int x, int y, int width, int height) {}
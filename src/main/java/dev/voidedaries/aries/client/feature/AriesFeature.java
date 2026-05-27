package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.config.AriesConfigType;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class AriesFeature {
    private final Component name;
    private final Component description;
    private final AriesCategory category;
    private final List<AriesConfigType<?>> configs = new ArrayList<>();

    public AriesFeature(Component name, Component description, AriesCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Component getName() {
        return name;
    }

    protected <T extends AriesConfigType<?>> T addConfig(T config) {
        configs.add(config);
        return config;
    }

    public List<AriesConfigType<?>> getConfigs() {
        return configs;
    }

    public Component getDescription() {
        return description;
    }

    public AriesCategory getCategory() {
        return category;
    }
}

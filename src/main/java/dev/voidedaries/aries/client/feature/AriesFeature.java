package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.entry.FeatureEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AriesFeature {

    private final Component name;
    private final Component description;
    private final AriesCategory category;

    private final List<AriesConfigType<?>> configs = new ArrayList<>();
    private final List<FeatureEntry> entries = new ArrayList<>();

    private Supplier<Boolean> visibleCondition = () -> true;

    public AriesFeature(Component name, Component description, AriesCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T extends AriesConfigType<?>> T addConfig(T config) {
        config.setFeature(this);
        configs.add(config);
        return config;
    }

    protected FeatureEntry addEntry(
        Component name,
        Component description,
        AriesConfigType<?>... configs
    ) {
        for (AriesConfigType<?> config : configs) {
            config.setFeature(this);
        }

        FeatureEntry entry = new FeatureEntry(name, description, List.of(configs));

        entries.add(entry);

        return entry;
    }

    public List<AriesConfigType<?>> getConfigs() {
        return configs;
    }

    public List<FeatureEntry> getEntries() {
        return entries;
    }

    public Component getName() {
        return name;
    }

    public Component getDescription() {
        return description;
    }

    public AriesCategory getCategory() {
        return category;
    }

    public boolean isVisible() {
        return visibleCondition.get();
    }

    public AriesFeature visibleWhen(Supplier<Boolean> condition) {
        this.visibleCondition = condition;
        return this;
    }
}

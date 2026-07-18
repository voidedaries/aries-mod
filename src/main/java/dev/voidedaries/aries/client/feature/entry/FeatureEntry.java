package dev.voidedaries.aries.client.feature.entry;

import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class FeatureEntry {

    private final Component name;
    private final Component description;
    private final List<AriesConfigType<?>> configs;

    private Supplier<Boolean> visibleCondition = () -> true;

    public FeatureEntry(Component name, Component description, List<AriesConfigType<?>> configs) {
        configs = List.copyOf(configs);
        this.name = name;
        this.description = description;
        this.configs = configs;
    }

    public FeatureEntry visibleWhen(Supplier<Boolean> condition) {
        this.visibleCondition = condition;
        return this;
    }

    public boolean isVisible() {
        return visibleCondition.get();
    }

    public Component name() {
        return name;
    }

    public Component description() {
        return description;
    }

    public List<AriesConfigType<?>> configs() {
        return configs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FeatureEntry) obj;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.description, that.description) &&
            Objects.equals(this.configs, that.configs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, configs);
    }

    @Override
    public String toString() {
        return "FeatureEntry[" +
            "name=" + name + ", " +
            "description=" + description + ", " +
            "configs=" + configs + ']';
    }

}

package dev.voidedaries.aries.client.feature.entry;

import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a grouped configuration entry displayed inside an Aries feature.
 *
 * <p>Feature entries allow multiple configurations to share a name,
 * description, visibility condition, and menu placement.</p>
 */
public final class FeatureEntry {

    private final Component name;
    private final Component description;
    private final List<AriesConfigType<?>> configs;

    private Supplier<Boolean> visibleCondition = () -> true;

    /**
     * Creates a new feature entry.
     *
     * @param name display name of the entry
     * @param description explanation of the entry
     * @param configs configurations belonging to this entry
     */
    public FeatureEntry(Component name, Component description, List<AriesConfigType<?>> configs) {
        configs = List.copyOf(configs);
        this.name = name;
        this.description = description;
        this.configs = configs;
    }

    /**
     * Controls when this entry appears in the menu.
     *
     * @param condition visibility condition
     * @return this entry for chaining
     */
    public FeatureEntry visibleWhen(Supplier<Boolean> condition) {
        this.visibleCondition = condition;
        return this;
    }

    /**
     * Checks whether this entry should be displayed.
     *
     * @return true if visible
     */
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

}

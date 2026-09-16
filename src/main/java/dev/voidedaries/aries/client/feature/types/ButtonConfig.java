package dev.voidedaries.aries.client.feature.types;

import net.minecraft.network.chat.Component;

import java.util.Objects;

public class ButtonConfig extends AriesConfigType<Void> {

    private final Component label;
    private final Runnable action;

    public ButtonConfig(Component label, Runnable action) {
        super("", null);
        this.label = Objects.requireNonNull(label);
        this.action = Objects.requireNonNull(action);
    }

    public Component getLabel() {
        return label;
    }

    public void press() {
        action.run();
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.BUTTON;
    }
}

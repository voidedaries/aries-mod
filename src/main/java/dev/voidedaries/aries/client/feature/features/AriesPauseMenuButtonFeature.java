package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class AriesPauseMenuButtonFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("pause_menu_button.enabled", true)
    );

    public AriesPauseMenuButtonFeature() {
        super(
            Component.translatable("gui.category.settings.pause_menu_button.name"),
            Component.translatable("gui.category.settings.pause_menu_button.description"),
            AriesCategory.SETTINGS
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class HypixelPauseMenuButtonFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("hypixel_pause_menu_button.enabled", true)
    );

    public HypixelPauseMenuButtonFeature() {
        super(
            Component.translatable("gui.category.misc.hypixel_pause_menu_button.name"),
            Component.translatable("gui.category.misc.hypixel_pause_menu_button.description"),
            AriesCategory.MISC
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

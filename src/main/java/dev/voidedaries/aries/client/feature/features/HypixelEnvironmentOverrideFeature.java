package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class HypixelEnvironmentOverrideFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("hypixel_environment_override.enabled", true)
    );

    public HypixelEnvironmentOverrideFeature() {
        super(
            Component.translatable("gui.category.dev.hypixel_environment_override.name"),
            Component.translatable("gui.category.dev.hypixel_environment_override.description"),
            AriesCategory.DEV
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

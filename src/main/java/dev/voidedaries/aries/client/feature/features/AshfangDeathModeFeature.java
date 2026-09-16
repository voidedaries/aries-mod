package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class AshfangDeathModeFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("ashfang_death_mode.enabled", true)
    );

    public AshfangDeathModeFeature() {
        super(
            Component.translatable("gui.category.crimson_isle.ashfang_death_mode.name"),
            Component.translatable("gui.category.crimson_isle.ashfang_death_mode.description"),
            AriesCategory.CRIMSON_ISLE
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.FloatConfig;
import net.minecraft.network.chat.Component;

public class LowFireFeature extends AriesFeature {
    public final FloatConfig offset = addConfig(
        new FloatConfig("low_fire.enabled", 0f, -0.5f, 0f, 0.1f)
    );

    public LowFireFeature() {
        super(
            Component.translatable("gui.category.visuals.low_fire.name"),
            Component.translatable("gui.category.visuals.low_fire.description"),
            AriesCategory.VISUALS
        );
    }

}

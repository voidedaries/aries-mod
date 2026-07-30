package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class RemoveSecondLayerArmorFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("second_layer_leather_armor.enabled", true)
    );

    public RemoveSecondLayerArmorFeature() {
        super(
            Component.translatable("gui.category.visuals.second_layer_leather_armor.name"),
            Component.translatable("gui.category.visuals.second_layer_leather_armor.description"),
            AriesCategory.VISUALS
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class ThirdPersonNameTagFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("third_person_nametag.enabled", true)
    );

    public ThirdPersonNameTagFeature() {
        super(
            Component.translatable("gui.category.visuals.third_person_nametag.name"),
            Component.translatable("gui.category.visuals.third_person_nametag.description"),
            AriesCategory.VISUALS
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

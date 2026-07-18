package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class OldMasterStarColoursFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("old_master_star_colours.enabled", true)
    );

    public OldMasterStarColoursFeature() {
        super(
            Component.translatable("gui.category.visuals.old_master_star_colours.name"),
            Component.translatable("gui.category.visuals.old_master_star_colours.description"),
            AriesCategory.VISUALS
        );
    }

}

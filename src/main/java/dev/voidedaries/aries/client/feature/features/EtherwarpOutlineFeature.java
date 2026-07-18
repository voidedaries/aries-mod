package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.config.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.config.BooleanConfig;
import net.minecraft.network.chat.Component;

public class EtherwarpOutlineFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("etherwarp_outline.enabled", true)
    );

    public EtherwarpOutlineFeature() {
        super(
            Component.translatable("gui.category.visuals.etherwarp_outline.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.description"),
            AriesCategory.VISUALS
        );
    }

}

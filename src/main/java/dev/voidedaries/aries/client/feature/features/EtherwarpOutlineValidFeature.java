package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.config.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.config.ColorConfig;
import net.minecraft.network.chat.Component;

public class EtherwarpOutlineValidFeature extends AriesFeature {
    public final ColorConfig etherwarpValidColor =
        addConfig(new ColorConfig("etherwarp_outline.valid_color", 0xFF0000FF));

    public EtherwarpOutlineValidFeature() {
        super(
            Component.translatable("gui.category.visuals.etherwarp_outline.valid_color.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.valid_color.description"),
            AriesCategory.VISUALS
        );
        this.visibleWhen(AriesFeatures.ETHERWARP_OUTLINE.enabled::get);
    }
}

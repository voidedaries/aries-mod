package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.config.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.config.ColorConfig;
import net.minecraft.network.chat.Component;

public class EtherwarpOutlineInvalidFeature extends AriesFeature {
    public final ColorConfig etherwarpInvalidColor =
        addConfig(new ColorConfig("etherwarp_outline.invalid_color", 0xFFFF0000));

    public EtherwarpOutlineInvalidFeature() {
        super(
            Component.translatable("gui.category.visuals.etherwarp_outline.invalid_color.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.invalid_color.description"),
            AriesCategory.VISUALS
        );
        this.visibleWhen(AriesFeatures.ETHERWARP_OUTLINE.enabled::get);
    }
}

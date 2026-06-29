package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.config.types.ColorConfig;
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
    }
}

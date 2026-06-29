package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.config.types.ColorConfig;
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
    }
}

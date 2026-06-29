package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.config.types.IntConfig;
import net.minecraft.network.chat.Component;

public class EtherwarpOutlineWidthFeature extends AriesFeature {
    public final IntConfig etherwarpWidth = addConfig(new IntConfig("etherwarp_outline.width", 2, 1, 5));

    public EtherwarpOutlineWidthFeature() {
        super(
            Component.translatable("gui.category.visuals.etherwarp_outline.width.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.width.description"),
            AriesCategory.VISUALS
        );
    }
}

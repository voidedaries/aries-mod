package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.config.ColorConfig;
import dev.voidedaries.aries.client.feature.config.IntConfig;
import dev.voidedaries.aries.client.feature.config.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.config.BooleanConfig;
import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;
import net.minecraft.network.chat.Component;

public class EtherwarpOutlineFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("etherwarp_outline.enabled", true)
    );

    public final IntConfig width = new IntConfig("etherwarp_outline.width", 2, 1, 5);

    public final ColorConfig valid = new ColorConfig("etherwarp_outline.valid_color", 0xFF0000FF);

    public final ColorConfig invalid = new ColorConfig("etherwarp_outline.invalid_color", 0xFFFF0000);

    public EtherwarpOutlineFeature() {
        super(
            Component.translatable("gui.category.visuals.etherwarp_outline.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.description"),
            AriesCategory.VISUALS
        );

        add("width", width);
        add("valid_color", valid);
        add("invalid_color", invalid);
    }

    private void add(String id, AriesConfigType<?> config) {
        addEntry(
            Component.translatable("gui.category.visuals.etherwarp_outline." + id + ".name"),
            Component.translatable("gui.category.visuals.etherwarp_outline." + id + ".description"),
            config
        ).visibleWhen(enabled::get);
    }

}

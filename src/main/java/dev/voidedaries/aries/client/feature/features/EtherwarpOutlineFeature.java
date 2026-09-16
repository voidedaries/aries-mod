package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.types.*;
import dev.voidedaries.aries.client.feature.AriesFeature;
import net.minecraft.network.chat.Component;

public class EtherwarpOutlineFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("etherwarp_outline.enabled", true)
    );

    public enum EtherwarpMode {
        FILL,
        OUTLINE
    }

    public static final ListConfig<EtherwarpMode> mode =
        new ListConfig<>(
            "etherwarp_outline.mode", EtherwarpMode.OUTLINE,
            EtherwarpMode.FILL, EtherwarpMode.OUTLINE
        );

    public final IntConfig width = new IntConfig("etherwarp_outline.width", 3, 1, 5);

    public final ColorConfig valid = new ColorConfig("etherwarp_outline.valid_color", 0xFF00FF00);

    public final ColorConfig invalid = new ColorConfig("etherwarp_outline.invalid_color", 0xFFFF0000);

    public EtherwarpOutlineFeature() {
        super(
            Component.translatable("gui.category.visuals.etherwarp_outline.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.description"),
            AriesCategory.VISUALS
        );

        addEntry(
            Component.translatable("gui.category.visuals.etherwarp_outline.mode.name"),
            Component.translatable("gui.category.visuals.etherwarp_outline.mode.description"),
            mode
        ).visibleWhen(enabled::get);

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

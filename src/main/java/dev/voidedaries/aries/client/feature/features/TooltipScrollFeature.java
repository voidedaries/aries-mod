package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class TooltipScrollFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("tooltip_scroll.enabled", false)
    );

    public final BooleanConfig smooth_scroll = new BooleanConfig("tooltip_scroll.smooth_scroll", false);

    public TooltipScrollFeature() {
        super(
            Component.translatable("gui.category.misc.tooltip_scroll.name"),
            Component.translatable("gui.category.misc.tooltip_scroll.description"),
            AriesCategory.MISC
        );

        addEntry(
            Component.translatable("gui.category.misc.tooltip_scroll.smooth_scroll.name"),
            Component.translatable("gui.category.misc.tooltip_scroll.smooth_scroll.description"),
            smooth_scroll
        ).visibleWhen(enabled::get);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

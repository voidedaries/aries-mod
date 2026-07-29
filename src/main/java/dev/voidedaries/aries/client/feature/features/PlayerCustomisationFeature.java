package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import dev.voidedaries.aries.client.feature.types.FloatConfig;
import net.minecraft.network.chat.Component;

public class PlayerCustomisationFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("player_customisation.enabled", true)
    );

    public final FloatConfig scale =
        new FloatConfig("player_customisation.scale", 0.0F, -5.0F, 5.0F, 0.1f);

    public final FloatConfig height =
        new FloatConfig("player_customisation.height", 0.0F, -5.0F, 5.0F, 0.1f);

    public final FloatConfig width =
        new FloatConfig("player_customisation.width", 0.0F, -5.0F, 5.0F, 0.1f);

    public final FloatConfig depth =
        new FloatConfig("player_customisation.depth", 0.0F, -5.0F, 5.0F, 0.1f);

    public PlayerCustomisationFeature() {
        super(
            Component.translatable("gui.category.visuals.player_customisation.name"),
            Component.translatable("gui.category.visuals.player_customisation.description"),
            AriesCategory.VISUALS
        );

        addEntry(
            Component.translatable("gui.category.visuals.player_customisation.scale.name"),
            Component.translatable("gui.category.visuals.player_customisation.scale.description"),
            scale
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.player_customisation.height.name"),
            Component.translatable("gui.category.visuals.player_customisation.height.description"),
            height
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.player_customisation.width.name"),
            Component.translatable("gui.category.visuals.player_customisation.width.description"),
            width
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.player_customisation.depth.name"),
            Component.translatable("gui.category.visuals.player_customisation.depth.description"),
            depth
        ).visibleWhen(enabled::get);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

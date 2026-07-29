package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import dev.voidedaries.aries.client.feature.types.FloatConfig;
import net.minecraft.network.chat.Component;

public class HeldItemCustomisationFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("held_item_customisation.enabled", true)
    );

    public final FloatConfig scale =
        new FloatConfig("held_item_customisation.scale", 0.0F, -2.0F, 2.0F, 0.1f);

    public final FloatConfig positionX =
        new FloatConfig("held_item_customisation.position_x", 0.0F, -2.0F, 2.0F, 0.1f);

    public final FloatConfig positionY =
        new FloatConfig("held_item_customisation.position_y", 0.0F, -2.0F, 2.0F, 0.1f);

    public final FloatConfig positionZ =
        new FloatConfig("held_item_customisation.position_z", 0.0F, -2.0F, 2.0F, 0.1f);

    public final FloatConfig swingSpeed =
        new FloatConfig("held_item_customisation.swing_speed", 0.0F, -2.0F, 2.0F, 0.1f);

    public HeldItemCustomisationFeature() {
        super(
            Component.translatable("gui.category.visuals.held_item_customisation.name"),
            Component.translatable("gui.category.visuals.held_item_customisation.description"),
            AriesCategory.VISUALS
        );

        addEntry(
            Component.translatable("gui.category.visuals.held_item_customisation.scale.name"),
            Component.translatable("gui.category.visuals.held_item_customisation.scale.description"),
            scale
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.held_item_customisation.position_x.name"),
            Component.translatable("gui.category.visuals.held_item_customisation.position_x.description"),
            positionX
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.held_item_customisation.position_y.name"),
            Component.translatable("gui.category.visuals.held_item_customisation.position_y.description"),
            positionY
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.held_item_customisation.position_z.name"),
            Component.translatable("gui.category.visuals.held_item_customisation.position_z.description"),
            positionZ
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.visuals.held_item_customisation.swing_speed.name"),
            Component.translatable("gui.category.visuals.held_item_customisation.swing_speed.description"),
            swingSpeed
        ).visibleWhen(enabled::get);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

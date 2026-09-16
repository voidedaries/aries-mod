package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.*;
import net.minecraft.network.chat.Component;

public class BlazingSoulTrajectoryFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("blazing_soul_trajectory.enabled", true)
    );

    public final FloatConfig width =
        new FloatConfig("blazing_soul_trajectory.width", 0.1f, 0f, 0.5f, 0.1f);

    public final ColorConfig color = new ColorConfig("blazing_soul_trajectory.color", 0xFFFF5300);

    public BlazingSoulTrajectoryFeature() {
        super(
            Component.translatable("gui.category.crimson_isle.blazing_soul_trajectory.name"),
            Component.translatable("gui.category.crimson_isle.blazing_soul_trajectory.description"),
            AriesCategory.CRIMSON_ISLE
        );

        addEntry(
            Component.translatable("gui.category.crimson_isle.blazing_soul_trajectory.color.name"),
            Component.translatable("gui.category.crimson_isle.blazing_soul_trajectory.color.description"),
            color
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.crimson_isle.blazing_soul_trajectory.width.name"),
            Component.translatable("gui.category.crimson_isle.blazing_soul_trajectory.width.description"),
            width
        ).visibleWhen(enabled::get);
    }

}

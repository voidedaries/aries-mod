package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class MinecraftWarningLogSuppressionFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("minecraft_warning_log_suppression.enabled", true)
    );

    public MinecraftWarningLogSuppressionFeature() {
        super(
            Component.translatable("gui.category.dev.minecraft_warning_log_suppression.name"),
            Component.translatable("gui.category.dev.minecraft_warning_log_suppression.description"),
            AriesCategory.DEV
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

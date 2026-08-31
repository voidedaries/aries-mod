package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class YggdrasilLogSuppressionFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("yggdrasil_log_suppression.enabled", true)
    );

    public YggdrasilLogSuppressionFeature() {
        super(
            Component.translatable("gui.category.dev.yggdrasil_log_suppression.name"),
            Component.translatable("gui.category.dev.yggdrasil_log_suppression.description"),
            AriesCategory.DEV
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

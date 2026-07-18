package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.config.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.config.BooleanConfig;
import net.minecraft.network.chat.Component;

public class CompactChatFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("compact_chat.enabled", true)
    );

    public CompactChatFeature() {
        super(
            Component.translatable("gui.category.chat.compact_chat.name"),
            Component.translatable("gui.category.chat.compact_chat.description"),
            AriesCategory.CHAT
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

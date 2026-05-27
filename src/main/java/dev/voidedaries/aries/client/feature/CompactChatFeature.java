package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.config.BooleanConfig;
import dev.voidedaries.aries.client.feature.config.IntConfig;
import net.minecraft.network.chat.Component;

public class CompactChatFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("compact_chat.enabled", true)
    );
    public final IntConfig compactChatTime = addConfig(
        new IntConfig("compact_chat.compact_chat_time", 60, 0, Integer.MAX_VALUE)
    );

    public CompactChatFeature() {
        super(
            Component.translatable("gui.category.chat.compact_chat.name"),
            Component.translatable("gui.category.chat.compact_chat.description"),
            AriesCategory.CHAT
        );
    }

    public boolean isEnabled() {
        return enabled.get() && compactChatTime.get() > 0;
    }

    public int getCompactTimeSeconds() {
        return compactChatTime.get();
    }

}

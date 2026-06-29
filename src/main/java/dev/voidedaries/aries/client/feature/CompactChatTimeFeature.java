package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.config.types.IntConfig;
import net.minecraft.network.chat.Component;

public class CompactChatTimeFeature extends AriesFeature {
    public final IntConfig compactChatTime = addConfig(
        new IntConfig("compact_chat.compact_chat_time", 60, 0, 120)
    );

    public CompactChatTimeFeature() {
        super(
            Component.translatable("gui.category.chat.compact_chat_time.name"),
            Component.translatable("gui.category.chat.compact_chat_time.description"),
            AriesCategory.CHAT
        );
    }

    public int getCompactTimeSeconds() {
        return compactChatTime.get();
    }

}

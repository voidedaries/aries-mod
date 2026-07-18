package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.types.IntConfig;
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
        this.visibleWhen(AriesFeatures.COMPACT_CHAT.enabled::get);
    }

    public int getCompactTimeSeconds() {
        return compactChatTime.get();
    }

}

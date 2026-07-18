package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class ScrollPeekChatFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("scroll_peek_chat.keybind", true)
    );

    public ScrollPeekChatFeature() {
        super(
            Component.translatable("gui.category.chat.scroll_peek_chat.name"),
            Component.translatable("gui.category.chat.scroll_peek_chat.description"),
            AriesCategory.CHAT
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

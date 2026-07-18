package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.KeybindConfig;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class PeekChatFeature extends AriesFeature {
    public final KeybindConfig keybind = addConfig(
        new KeybindConfig("peek_chat.keybind", GLFW.GLFW_KEY_Z)
    );

    public PeekChatFeature() {
        super(
            Component.translatable("gui.category.chat.peek_chat.name"),
            Component.translatable("gui.category.chat.peek_chat.description"),
            AriesCategory.CHAT
        );
    }

    public boolean isDown() {
        return keybind.isDown();
    }

}

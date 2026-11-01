package dev.voidedaries.aries.client.message;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ChatMessage {
    private static final Queue<String> MESSAGES = new ConcurrentLinkedQueue<>();

    private ChatMessage() {}

    public static void init() {
        ClientReceiveMessageEvents.GAME.register(ChatMessage::onGameMessage);
    }

    private static void onGameMessage(Component message, boolean overlay) {
        MESSAGES.add(message.getString());
    }

    public static boolean contains(String text) {
        String message;

        while ((message = MESSAGES.poll()) != null) {
            if (message.contains(text)) {
                return true;
            }
        }

        return false;
    }
}
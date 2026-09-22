package dev.voidedaries.aries.client.message;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class AriesMessage {

    private AriesMessage() {}

    public static void sendPlayerMessage(Component message) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        MutableComponent prefix = ComponentHelper.gradient("[Aries]", 0x00E5FF, 0x0058E1);

        minecraft.player.sendSystemMessage(prefix.append(Component.literal(" ").append(message)));
    }

    public static void sendPlayerInfoMessage(Component message) {
        sendPlayerMessage(message.copy().withStyle(ChatFormatting.AQUA));
    }

    public static void sendPlayerWarnMessage(Component message) {
        sendPlayerMessage(message.copy().withStyle(ChatFormatting.YELLOW));
    }

    public static void sendPlayerErrorMessage(Component message) {
        sendPlayerMessage(message.copy().withStyle(ChatFormatting.RED));
    }

}

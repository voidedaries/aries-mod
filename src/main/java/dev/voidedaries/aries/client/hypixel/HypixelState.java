package dev.voidedaries.aries.client.hypixel;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.hypixel.data.type.GameType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.util.Locale;

public class HypixelState {
    private static boolean isInSkyblock = false;

    public HypixelState() {
    }

    public static void register() {
        HypixelModAPI hypixelModAPI = HypixelModAPI.getInstance();
        hypixelModAPI.createHandler(ClientboundLocationPacket.class, event ->
            isInSkyblock = event.getServerType()
            .filter(serverType -> serverType instanceof GameType)
            .map(serverType -> serverType == GameType.SKYBLOCK)
            .orElse(false));

        hypixelModAPI.subscribeToEventPacket(ClientboundLocationPacket.class);
    }

    public static boolean isPlayerInSkyblock() {
        if (AriesFeatures.HYPIXEL_ENVIRONMENT_OVERRIDE.isEnabled()) {
            return true;
        }

        return isInSkyblock;
    }

    public static  boolean isOnHypixelNetwork() {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData serverData = minecraft.getCurrentServer();

        if (AriesFeatures.HYPIXEL_ENVIRONMENT_OVERRIDE.isEnabled()) {
            return true;
        }

        if (serverData == null) {
            return false;
        }

        return serverData.ip.toLowerCase(Locale.ROOT).contains("hypixel.net");
    }
}

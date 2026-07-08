package dev.voidedaries.aries.client.event;

import net.hypixel.data.type.GameType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class SkyblockTracker {
    private static boolean isInSkyblock = false;

    public SkyblockTracker() {
    }

    public static void register() {
        HypixelModAPI hypixelModAPI = HypixelModAPI.getInstance();
        hypixelModAPI.createHandler(ClientboundLocationPacket.class, (event) -> {
            boolean newStatus = event.getServerType().filter(
                (serverType) -> serverType instanceof GameType
            ).map((serverType) -> serverType == GameType.SKYBLOCK).orElse(false);

            if (isInSkyblock != newStatus) {
                isInSkyblock = newStatus;
            }

        });
        hypixelModAPI.subscribeToEventPacket(ClientboundLocationPacket.class);
    }

    public static boolean isPlayerInSkyblock() {
        return isInSkyblock;
    }
}

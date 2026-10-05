package dev.voidedaries.aries.skyblock.entity;

import dev.voidedaries.aries.Aries;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class CritterSafariChatHandler {
    private static final CritterSafari safari = new CritterSafari();

    private CritterSafariChatHandler() {}

    public static void init() {
        Aries.log("Critter Safari chat handler initialized");

        ClientReceiveMessageEvents.GAME.register((message, _) -> {
            String text = message.getString().replaceAll("§.", "");

            if (text.contains("[NPC] Safari Manager:") && text.contains("Have fun out there!")
            || text.contains("[NPC] Safari Manager:") && text.contains("so you're free to go.")) {
                if (!safari.isActive()) {
                    safari.start();
                }
            }

            if (text.contains("SAFARI REWARD SUMMARY")) {
                if (safari.isActive()) {
                    safari.end();
                }
            }

            if (text.contains("LOOT SHARE!") || text.contains("CAPTURE!")) {
                Critter critter = parseCritter(text);

                if (critter != null) {
                    safari.catchCritter(critter);
                }
            }
        });
    }

    private static Critter parseCritter(String text) {
        int shardIndex = text.indexOf(" Shard");

        if (shardIndex == -1) {
            return null;
        }

        String beforeShard = text.substring(0, shardIndex);

        int xIndex = beforeShard.lastIndexOf("x ");
        int receivedIndex = beforeShard.lastIndexOf("received ");
        int gainedIndex = beforeShard.lastIndexOf("gained ");

        String critterName = null;

        if (xIndex != -1) {
            critterName = beforeShard.substring(xIndex + 2);
        } else if (receivedIndex != -1) {
            critterName = beforeShard.substring(receivedIndex + 9);
        } else if (gainedIndex != -1) {
            critterName = beforeShard.substring(gainedIndex + 7);
        }

        if (critterName == null) {
            return null;
        }

        critterName = critterName.trim();

        if (critterName.startsWith("an ")) {
            critterName = critterName.substring(3);
        } else if (critterName.startsWith("a ")) {
            critterName = critterName.substring(2);
        }

        Aries.log("Parsed critter name: [" + critterName + "]");

        return Critter.fromName(critterName);
    }

    public static CritterSafari getSafari() {
        return safari;
    }

}

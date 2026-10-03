package dev.voidedaries.aries.skyblock.entity;

import dev.voidedaries.aries.Aries;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class CritterSafariChatHandler {
    private static final CritterSafari safari = new CritterSafari();

    private CritterSafariChatHandler() {}

    public static void init() {
        Aries.log("Critter Safari chat handler initialized");

        ClientReceiveMessageEvents.GAME.register((message, _) -> {
            String text = message.getString();

            if (text.contains("entered Critter Safari!")) {
                safari.start();
            }

            if (text.contains("SAFARI REWARD SUMMARY")) {
                safari.end();
            }

            if (text.contains("LOOT SHARE!")) {
                String critterName = text
                    .replaceAll(".*catching a ", "")
                    .replaceAll("§.", "")
                    .replaceAll("!$", "");

                Critter critter = Critter.fromName(critterName);

                if (critter != null) {
                    safari.catchCritter(critter);
                }
            }

            if (text.contains("CAPTURE!")) {
                String critterName = text
                    .replaceAll("§.", "")
                    .replaceAll(".*You caught (?:a|an) ", "")
                    .replaceAll(".*You found the ", "")
                    .replaceAll(",.*", "")
                    .replaceAll(" and gained.*", "");

                Critter critter = Critter.fromName(critterName);

                if (critter != null) {
                    safari.catchCritter(critter);
                }
            }
        });
    }

    public static CritterSafari getSafari() {
        return safari;
    }

}

package dev.voidedaries.aries.client.command;

import dev.voidedaries.aries.Aries;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class ClientCommandHooks {

    private static boolean spinning = false;

    public static void init() {
        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> {

            if (command.equalsIgnoreCase("spin")) {
                spinning = !spinning;

                Aries.log(spinning ? "Spin activated!" : "Spin disabled!");

                return false;
            }

            return true;
        });
    }

    public static boolean isSpinning() {
        return spinning;
    }

}

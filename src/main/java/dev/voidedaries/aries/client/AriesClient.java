package dev.voidedaries.aries.client;

import dev.voidedaries.aries.client.commands.AriesCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class AriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AriesConfig.init();

        ClientCommandRegistrationCallback.EVENT.register((
                (dispatcher, _)
                -> AriesCommands.register(dispatcher)));
    }
}

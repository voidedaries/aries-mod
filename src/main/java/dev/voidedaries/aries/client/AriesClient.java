package dev.voidedaries.aries.client;

import dev.voidedaries.aries.client.commands.AriesCommands;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class AriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        try {
            SuppressAuthlibSpam.init();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        AriesFeatures.init();
        AriesConfig.init();

        ClientCommandRegistrationCallback.EVENT.register((
                (dispatcher, _)
                -> AriesCommands.register(dispatcher)));
    }
}

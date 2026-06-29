package dev.voidedaries.aries.client;

import dev.voidedaries.aries.client.command.AriesCommands;
import dev.voidedaries.aries.client.event.SkyblockTracker;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.render.RenderManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

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

        WorldRenderEvents.AFTER_ENTITIES.register(RenderManager::render);

        (new Thread(new ConfigWatcher(), "Aries-ConfigWatcher")).start();

        SkyblockTracker.register();

        ClientCommandRegistrationCallback.EVENT.register((
                (dispatcher, _)
                -> AriesCommands.register(dispatcher)));
    }
}

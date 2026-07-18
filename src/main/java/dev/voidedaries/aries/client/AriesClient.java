package dev.voidedaries.aries.client;

import dev.voidedaries.aries.client.command.AriesCommands;
import dev.voidedaries.aries.client.event.SkyblockTracker;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.types.KeybindConfig;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.render.RenderManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class AriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AriesFeatures.init();

        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            for (AriesConfigType<?> config : feature.getConfigs()) {
                if (config instanceof KeybindConfig keybind) {
                    keybind.register();
                }
            }
        }

        AriesConfig.init();

        LevelRenderEvents.AFTER_SOLID_FEATURES.register(RenderManager::render);

        (new Thread(new ConfigWatcher(), "Aries-ConfigWatcher")).start();

        SkyblockTracker.register();

        ClientCommandRegistrationCallback.EVENT.register((
                (dispatcher, _)
                -> AriesCommands.register(dispatcher)));
    }
}

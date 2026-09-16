package dev.voidedaries.aries.client;

import dev.voidedaries.aries.client.command.AriesCommands;
import dev.voidedaries.aries.client.command.ClientCommandHooks;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.gui.location.AriesHudManager;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import dev.voidedaries.aries.client.keybind.KeybindManager;
import dev.voidedaries.aries.client.render.BlockRenderManager;
import dev.voidedaries.aries.client.render.EntityRendererManager;
import dev.voidedaries.aries.skyblock.entity.SkyblockEntityManager;
import dev.voidedaries.aries.skyblock.repo.SkyBlockRepoDownloader;
import net.fabricmc.api.ClientModInitializer;

public class AriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SkyBlockRepoDownloader.updateAsync();

        AriesFeatures.init();
        AriesConfig.init();

        AriesCommands.init();
        ClientCommandHooks.init();
        AriesHudManager.init();

        KeybindManager.init();

        HypixelState.register();

        BlockRenderManager.init();
        EntityRendererManager.init();
        SkyblockEntityManager.init();

        (new Thread(new ConfigWatcher(), "Aries-ConfigWatcher")).start();

    }
}

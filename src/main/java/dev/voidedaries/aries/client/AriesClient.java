package dev.voidedaries.aries.client;

import dev.voidedaries.aries.client.command.AriesCommands;
import dev.voidedaries.aries.client.command.ClientCommandHooks;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.gui.location.AriesHudManager;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import dev.voidedaries.aries.client.keybind.KeybindManager;
import dev.voidedaries.aries.client.render.BlockRenderManager;
import dev.voidedaries.aries.client.render.EntityRendererManager;
import dev.voidedaries.aries.client.update.AriesUpdateChecker;
import dev.voidedaries.aries.skyblock.entity.SkyblockEntityManager;
import dev.voidedaries.aries.skyblock.repo.SkyBlockRepoDownloader;
import net.fabricmc.api.ClientModInitializer;

public class AriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AriesFeatures.init();

        // config
        AriesConfig.init();
        AriesConfig.saveOnShutdown();
        ConfigWatcher.init();

        // client systems
        AriesHudManager.init();
        KeybindManager.init();
        HypixelState.register();

        // services
        AriesUpdateChecker.init();
        SkyBlockRepoDownloader.updateAsync();

        // commands
        AriesCommands.init();
        ClientCommandHooks.init();

        // rendering
        BlockRenderManager.init();
        EntityRendererManager.init();
        SkyblockEntityManager.init();
    }
}

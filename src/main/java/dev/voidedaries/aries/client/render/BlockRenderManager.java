package dev.voidedaries.aries.client.render;

import dev.voidedaries.aries.client.render.item.EtherwarpRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class BlockRenderManager {

    public static void init() {
        LevelRenderEvents.COLLECT_SUBMITS.register(BlockRenderManager::render);
    }


    public static void render(LevelRenderContext context) {
        EtherwarpRenderer.render(context);
    }

}

package dev.voidedaries.aries.client.render;

import dev.voidedaries.aries.client.render.item.EtherwarpRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;

public class RenderManager {

    public static void render(LevelRenderContext context) {
        EtherwarpRenderer.render(context);
    }

}

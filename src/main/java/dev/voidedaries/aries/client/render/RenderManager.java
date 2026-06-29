package dev.voidedaries.aries.client.render;

import dev.voidedaries.aries.client.render.item.EtherwarpRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

public class RenderManager {

    public static void render(WorldRenderContext context) {
        EtherwarpRenderer.render(context);
    }

}

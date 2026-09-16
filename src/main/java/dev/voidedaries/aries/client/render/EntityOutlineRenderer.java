package dev.voidedaries.aries.client.render;

import dev.voidedaries.aries.client.render.item.AshfangRenderer;
import net.minecraft.world.entity.Entity;

public class EntityOutlineRenderer {

    public static int getOutlineColor(Entity entity) {
        if (entity == null) {
            return 0;
        }

        return AshfangRenderer.getOutlineColor(entity);
    }

    private EntityOutlineRenderer() {}

}

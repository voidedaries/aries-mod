package dev.voidedaries.aries.client.render.item;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.skyblock.entity.SkyblockEntity;
import dev.voidedaries.aries.skyblock.entity.SkyblockEntityManager;
import net.minecraft.world.entity.Entity;

public class AshfangRenderer {

    private AshfangRenderer() {}

    public static int getOutlineColor(Entity entity) {
        SkyblockEntity skyblockEntity = SkyblockEntityManager.get(entity.getId());

        if (skyblockEntity == null) {
            return 0;
        }

        return switch (skyblockEntity.getName()) {
            case "Ashfang Follower" ->
                AriesFeatures.ASHFANG_MOBS_COLOR.acolyte.get();

            case "Ashfang Acolyte" ->
                AriesFeatures.ASHFANG_MOBS_COLOR.underling.get();

            case "Ashfang Underling" ->
                AriesFeatures.ASHFANG_MOBS_COLOR.follower.get();

            default -> 0;
        };
    }
}

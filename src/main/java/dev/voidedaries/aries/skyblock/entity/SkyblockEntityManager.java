package dev.voidedaries.aries.skyblock.entity;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkyblockEntityManager {

    private static final Map<Integer, SkyblockEntity> ENTITIES = new HashMap<>();
    private static final Set<SkyblockEntity> UNIQUE_ENTITIES = new HashSet<>();

    private SkyblockEntityManager() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientLevel world = client.level;

            if (world == null) {
                SkyblockEntityManager.clear();
                return;
            }

            for (Entity entity : world.entitiesForRendering()) {
                SkyblockEntityManager.registerEntity(world, entity);
            }
        });
    }

    public static void registerEntity(ClientLevel world, Entity entity) {

        if (entity instanceof ArmorStand armorStand) {
            checkNameTag(armorStand, world);
        }
    }

    private static void checkNameTag(ArmorStand nameTag, ClientLevel world) {
        if (!nameTag.hasCustomName()) {
            return;
        }

        LivingEntity model = findModel(nameTag, world);

        if (model == null) {
            return;
        }

        if (ENTITIES.containsKey(nameTag.getId())) {
            return;
        }

        if (ENTITIES.containsKey(model.getId())) {
            return;
        }

        SkyblockEntity skyblockEntity = new SkyblockEntity(nameTag, model);

        ENTITIES.put(nameTag.getId(), skyblockEntity);
        ENTITIES.put(model.getId(), skyblockEntity);

        UNIQUE_ENTITIES.add(skyblockEntity);
    }

    private static LivingEntity findModel(ArmorStand nameTag, ClientLevel world) {
        int modelId = nameTag.getId() + 1;

        Entity entity = world.getEntity(modelId);

        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }

        if (!livingEntity.isAlive() || livingEntity.isRemoved()) {
            return null;
        }

        return livingEntity;
    }

    public static SkyblockEntity get(int entityId) {
        return ENTITIES.get(entityId);
    }

    public static Set<SkyblockEntity> getEntities() {
        return Set.copyOf(UNIQUE_ENTITIES);
    }

    public static void remove(int entityId) {
        SkyblockEntity skyblockEntity = ENTITIES.get(entityId);

        if (skyblockEntity == null) {
            return;
        }

        for (LivingEntity model : skyblockEntity.getModelEntities()) {
            ENTITIES.remove(model.getId());
        }

        ENTITIES.remove(skyblockEntity.getNameTagEntity().getId());
        UNIQUE_ENTITIES.remove(skyblockEntity);
    }

    public static void clear() {
        ENTITIES.clear();
        UNIQUE_ENTITIES.clear();
    }
}

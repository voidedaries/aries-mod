package dev.voidedaries.aries.client.render.entity;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.render.LineRenderer;
import dev.voidedaries.aries.skyblock.entity.entities.BlazingSoul;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BlazingSoulRenderer {

    private static final double ASHFANG_X = -484.5;
    private static final double ASHFANG_Z = -1015.5;

    private static final double BLAZING_SOUL_Y_OFFSET = -0.5;

    private static final double ASHFANG_TOLERANCE = 2.0;

    private static Blaze ashfang;
    private static final List<Blaze> ASHFANG_ENTITIES = new ArrayList<>();
    private static boolean ashfangInTrajectory;

    private BlazingSoulRenderer() {}

    public static void update(ClientLevel level, List<BlazingSoul> blazingSouls) {
        ashfang = null;
        ASHFANG_ENTITIES.clear();
        ashfangInTrajectory = false;

        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof Blaze blaze)) {
                continue;
            }

            double xDistance = blaze.getX() - ASHFANG_X;
            double zDistance = blaze.getZ() - ASHFANG_Z;

            if (xDistance * xDistance + zDistance * zDistance > ASHFANG_TOLERANCE * ASHFANG_TOLERANCE) {
                continue;
            }

            ASHFANG_ENTITIES.add(blaze);

            if (ashfang == null) {
                ashfang = blaze;
            }
        }

        if (ashfang == null) {
            return;
        }

        for (BlazingSoul blazingSoul : blazingSouls) {
            if (!isInHittingDistance(blazingSoul)) {
                continue;
            }

            Vec3 actualStart = blazingSoul.getPosition(1.0F);
            Vec3 start = actualStart.add(0, BLAZING_SOUL_Y_OFFSET, 0);

            Vec3 direction = blazingSoul.getLaunchDirection().normalize();

            double distance = actualStart.distanceTo(ashfang.position());
            Vec3 end = start.add(direction.scale(distance));

            if (isAshfangInTrajectory(start, end)) {
                ashfangInTrajectory = true;
                return;
            }
        }
    }

    public static void render(BlazingSoul blazingSoul, LevelRenderContext context) {
        if (ashfang == null) {
            return;
        }

        if (!isInHittingDistance(blazingSoul)) {
            return;
        }

        Vec3 actualStart = blazingSoul.getPosition(1.0F);
        Vec3 start = actualStart.add(0, BLAZING_SOUL_Y_OFFSET, 0);

        Vec3 direction = blazingSoul.getLaunchDirection().normalize();

        double distance = actualStart.distanceTo(ashfang.position());
        Vec3 end = start.add(direction.scale(distance));

        LineRenderer.renderLine(
            context,
            start, end,
            AriesFeatures.BLAZING_SOUL_TRAJECTORY.color.get(), AriesFeatures.BLAZING_SOUL_TRAJECTORY.width.get()
        );
    }

    private static boolean isInHittingDistance(BlazingSoul blazingSoul) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return false;
        }

        return minecraft.player.isWithinEntityInteractionRange(
            blazingSoul.getEntity(), minecraft.player.entityInteractionRange()
        );
    }

    private static boolean isAshfangInTrajectory(Vec3 start, Vec3 end) {
        for (Blaze blaze : ASHFANG_ENTITIES) {
            if (blaze.getBoundingBox().clip(start, end).isPresent()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAshfang(Entity entity) {
        return entity instanceof Blaze blaze && ASHFANG_ENTITIES.contains(blaze);
    }

    public static boolean isAshfangInTrajectory() {
        return ashfangInTrajectory;
    }

}

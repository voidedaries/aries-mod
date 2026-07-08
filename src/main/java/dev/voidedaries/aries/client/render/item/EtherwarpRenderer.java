package dev.voidedaries.aries.client.render.item;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.render.BlockRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EtherwarpRenderer {

    private static final double ETHERWARP_RANGE = 61;

    private enum TargetState {
        VALID,
        BLOCKED,
        INVALID_BLOCK,
        OUT_OF_RANGE
    }

    public static void render(LevelRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (!AriesFeatures.ETHERWARP_OUTLINE.enabled.get()) {
            return;
        }

        if (!player.isShiftKeyDown()) {
            return;
        }
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        BlockHitResult hit = raycast(player);

        if (hit == null) {
            return;
        }

        if (hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        AABB box = new AABB(pos);

        double distance = hit.getLocation()
            .distanceTo(player.getEyePosition(partialTick));

        TargetState state = getTargetState(player, pos, distance);

        switch (state) {
            case VALID -> BlockRenderer.renderBlockOutline(
                context, box,
                AriesFeatures.ETHERWARP_OUTLINE_VALID.etherwarpValidColor.get(),
                AriesFeatures.ETHERWARP_OUTLINE_WIDTH.etherwarpWidth.get()
            );

            case BLOCKED, INVALID_BLOCK -> BlockRenderer.renderBlockOutline(
                context, box,
                AriesFeatures.ETHERWARP_OUTLINE_INVALID.etherwarpInvalidColor.get(),
                AriesFeatures.ETHERWARP_OUTLINE_WIDTH.etherwarpWidth.get()
            );

            case OUT_OF_RANGE -> {}
        }
    }

    private static TargetState getTargetState(Player player, BlockPos pos, double distance) {
        var level = player.level();

        //out of range
        if (distance > ETHERWARP_RANGE) {
            return TargetState.OUT_OF_RANGE;
        }

        var state = level.getBlockState(pos);

        //ignore foliage/non-solid blocks
        if (state.getCollisionShape(level, pos).isEmpty()) {
            return TargetState.INVALID_BLOCK;
        }

        //free space above
        if (!level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
            return TargetState.BLOCKED;
        }

        return TargetState.VALID;
    }

    private static BlockHitResult raycast(Player player) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return null;
        }

        Camera camera = minecraft.gameRenderer.mainCamera();

        Vec3 start = camera.position();
        Vec3 look = new Vec3(camera.forwardVector());

        Vec3 end = start.add(look.scale(ETHERWARP_RANGE));

        return player.level().clip(new ClipContext(
            start,
            end,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
    }

    public static boolean isActive() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return false;
        }

        return AriesFeatures.ETHERWARP_OUTLINE.enabled.get() && player.isShiftKeyDown();
    }
}
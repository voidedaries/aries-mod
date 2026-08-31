package dev.voidedaries.aries.client.render.item;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.render.BlockRenderer;
import dev.voidedaries.aries.skyblock.SkyblockItem;
import dev.voidedaries.aries.skyblock.SkyblockItemLookup;
import dev.voidedaries.aries.skyblock.SkyblockItemUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class EtherwarpRenderer {

    public static boolean isEtherwarpItem(ItemStack stack) {
        return getEtherwarpRange(stack).isPresent();
    }

    private static SkyblockItem getEtherwarpItem(ItemStack stack) {
        String itemName = stack.getHoverName().getString();

        if (SkyblockItemUtils.containsDisplayName(SkyblockItemLookup.ASPECT_OF_THE_END, itemName)) {
            return SkyblockItemLookup.ASPECT_OF_THE_END.getSkyblockItem();
        }

        if (SkyblockItemUtils.containsDisplayName(SkyblockItemLookup.ASPECT_OF_THE_VOID, itemName)) {
            return SkyblockItemLookup.ASPECT_OF_THE_VOID.getSkyblockItem();
        }

        return null;
    }

    private static OptionalDouble getEtherwarpRange(List<Component> lore) {
        Optional<String> teleportLine =
            SkyblockItemUtils.findLoreTextAfter(lore, "Ability: Ether Transmission", "to ");

        if (teleportLine.isEmpty()) {
            return OptionalDouble.empty();
        }

        String[] parts = teleportLine.get().split(" ");

        try {
            return OptionalDouble.of(Double.parseDouble(parts[1]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            return OptionalDouble.empty();
        }
    }

    private static OptionalDouble getEtherwarpRange(SkyblockItem item) {
        return getEtherwarpRange(item.lore());
    }

    private static List<Component> getHeldItemLore(ItemStack stack) {
        ItemLore lore = stack.get(DataComponents.LORE);

        if (lore == null) {
            return List.of();
        }

        return lore.lines();
    }

    private static OptionalDouble getEtherwarpRange(ItemStack stack) {
        return getEtherwarpRange(getHeldItemLore(stack));
    }

    private static double getEffectiveEtherwarpRange(SkyblockItem item, ItemStack stack) {
        double repoRange = getEtherwarpRange(item).orElse(0);
        double heldRange = getEtherwarpRange(stack).orElse(0);

        return Math.max(repoRange, heldRange);
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

        ItemStack heldItem = player.getMainHandItem();

        SkyblockItem etherwarpItem = getEtherwarpItem(heldItem);

        if (etherwarpItem == null) {
            return;
        }

        double etherwarpRange = getEffectiveEtherwarpRange(etherwarpItem, heldItem);

        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        BlockHitResult hit = raycast(player, etherwarpRange);

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

        TargetState state = getTargetState(player, pos, distance, etherwarpRange);

        switch (state) {
            case VALID -> BlockRenderer.renderBlockOutline(
                context, box,
                AriesFeatures.ETHERWARP_OUTLINE.valid.get(),
                AriesFeatures.ETHERWARP_OUTLINE.width.get()
            );

            case BLOCKED, INVALID_BLOCK -> BlockRenderer.renderBlockOutline(
                context, box,
                AriesFeatures.ETHERWARP_OUTLINE.invalid.get(),
                AriesFeatures.ETHERWARP_OUTLINE.width.get()
            );

            case OUT_OF_RANGE -> {}
        }
    }

    private enum TargetState {
        VALID,
        BLOCKED,
        INVALID_BLOCK,
        OUT_OF_RANGE
    }

    private static TargetState getTargetState(Player player, BlockPos pos, double distance, double range) {
        Level level = player.level();

        //out of range
        if (distance > range) {
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

    private static BlockHitResult raycast(Player player, double range) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return null;
        }

        Camera camera = minecraft.gameRenderer.mainCamera();

        Vec3 start = camera.position();
        Vec3 look = new Vec3(camera.forwardVector());

        Vec3 end = start.add(look.scale(range));
        return minecraft.level.clip(new ClipContext(
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
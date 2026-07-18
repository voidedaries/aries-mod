package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.accessor.ItemEntityRenderStateAccessor;
import dev.voidedaries.aries.client.event.SkyblockTracker;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.item.ItemRarity;
import dev.voidedaries.aries.client.item.ItemRarityScales;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V", at = @At("TAIL"))
    private void aries$attachRarity(
        ItemEntity entity,
        ItemEntityRenderState state,
        float partialTicks,
        CallbackInfo ci
    ) {
        ItemStack stack = entity.getItem();

        List<Component> tooltip = stack.getTooltipLines(
            Item.TooltipContext.EMPTY,
            Minecraft.getInstance().player,
            TooltipFlag.NORMAL
        );

        ItemRarity rarity = ItemRarity.UNKNOWN;

        if (!tooltip.isEmpty()) {
            String lastTooltipLine = tooltip.getLast().toString();
            rarity = ItemRarity.fromText(lastTooltipLine);
        }

        ((ItemEntityRenderStateAccessor) state).aries$setRarity(rarity);
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    private void aries$scale(
        ItemEntityRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        CameraRenderState camera,
        CallbackInfo ci
    ) {
        if (!AriesFeatures.ITEM_RARITY_SCALING.enabled.get() || !SkyblockTracker.isPlayerInSkyblock()) {
            return;
        }

        ItemRarity rarity = ((ItemEntityRenderStateAccessor) state).aries$getRarity();
        float scale = ItemRarityScales.get(rarity);

        poseStack.scale(scale, scale, scale);
    }

}

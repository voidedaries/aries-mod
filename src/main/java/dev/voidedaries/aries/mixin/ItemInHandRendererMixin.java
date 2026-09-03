package dev.voidedaries.aries.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(
        //? if 26.2
        method = "submitArmWithItem",
        //? if <26.2
        //method = "renderArmWithItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
        )
    )
    private void aries$customiseHeldItem(
        AbstractClientPlayer player,
        float frameInterp,
        float xRot,
        InteractionHand hand,
        float attack,
        ItemStack itemStack,
        float inverseArmHeight,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int lightCoords,
        CallbackInfo ci
    ) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return;
        }

        var feature = AriesFeatures.HELD_ITEM_CUSTOMISATION;

        float positionMultiplier = 0.25f;

        poseStack.translate(
            feature.positionX.get() * positionMultiplier,
            feature.positionY.get() * positionMultiplier,
            feature.positionZ.get() * positionMultiplier
        );

        float scaleModifier = feature.scale.get();

        float scale = (float) Math.pow(2, scaleModifier);

        poseStack.scale(scale, scale, scale);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemSwapScale(F)F"))
    private float aries$disableEquipAnimation(float original) {
        if (!AriesFeatures.HELD_ITEM_CUSTOMISATION.isEnabled()) {
            return original;
        }

        return 1.0f;
    }

}

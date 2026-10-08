package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.accessor.AvatarRenderStateAccessor;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("RETURN"), cancellable = true)
    private void aries$showOwnName(T entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        if (entity != minecraft.player) {
            return;
        }

        if (!AriesFeatures.THIRD_PERSON_NAME_TAG.isEnabled()) {
            return;
        }

        if (minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }

        cir.setReturnValue(true);
    }

    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void aries$handleLocalPlayerRenderState(
        T entity,
        LivingEntityRenderState state,
        float partialTicks,
        CallbackInfo ci
    ) {
        if (!(state instanceof AvatarRenderState avatarState)) {
            return;
        }

        AvatarRenderStateAccessor accessor = (AvatarRenderStateAccessor) avatarState;

        boolean isLocalPlayer = entity == Minecraft.getInstance().player;
        accessor.aries$setLocalPlayer(isLocalPlayer);

        if (!isLocalPlayer) {
            return;
        }

        if (!AriesFeatures.HIDE_ARMOR.isEnabled()) {
            return;
        }

        if (AriesFeatures.HIDE_ARMOR.isHidden(EquipmentSlot.HEAD)) {
            state.headItem.clear();
            state.wornHeadType = null;
            state.wornHeadProfile = null;
            avatarState.headEquipment = ItemStack.EMPTY;
        }

        if (AriesFeatures.HIDE_ARMOR.isHidden(EquipmentSlot.CHEST)) {
            avatarState.chestEquipment = ItemStack.EMPTY;
        }

        if (AriesFeatures.HIDE_ARMOR.isHidden(EquipmentSlot.LEGS)) {
            avatarState.legsEquipment = ItemStack.EMPTY;
        }

        if (AriesFeatures.HIDE_ARMOR.isHidden(EquipmentSlot.FEET)) {
            avatarState.feetEquipment = ItemStack.EMPTY;
        }
    }

}

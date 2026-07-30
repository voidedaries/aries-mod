package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<S extends HumanoidRenderState> {

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void aries$hideArmor(
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        ItemStack itemStack,
        EquipmentSlot slot,
        int lightCoords,
        S state,
        CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!AriesFeatures.HIDE_ARMOR.isEnabled()) {
            return;
        }

        if (minecraft.player == null) {
            return;
        }

        if (AriesFeatures.HIDE_ARMOR.isHidden(slot)) {
            ci.cancel();
        }
    }

}

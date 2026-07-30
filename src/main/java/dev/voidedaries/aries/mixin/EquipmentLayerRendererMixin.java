package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

    @ModifyVariable(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V", at = @At(value = "STORE"), name = "layers")
    private List<EquipmentClientInfo.Layer> aries$removeLeatherOverlay(List<EquipmentClientInfo.Layer> layers) {
        if (!AriesFeatures.REMOVE_SECOND_LAYER_ARMOR.isEnabled()) {
            return layers;
        }

        return layers.stream().filter(layer -> !layer.getTextureLocation(EquipmentClientInfo.LayerType.HUMANOID)
                .toString().contains("overlay")).toList();
    }

}

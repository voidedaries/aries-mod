package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.item.ItemRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void aries$disableItemCulling(
        T entity,
        Frustum culler,
        double camX,
        double camY,
        double camZ,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!(entity instanceof ItemEntity item)) {
            return;
        }

        ItemStack stack = item.getItem();

        List<Component> tooltip = stack.getTooltipLines(
            Item.TooltipContext.EMPTY,
            Minecraft.getInstance().player,
            TooltipFlag.NORMAL
        );

        if (!tooltip.isEmpty()) {
            String lastLine = tooltip.getLast().toString();

            if (ItemRarity.fromText(lastLine) != ItemRarity.UNCOMMON) {
                cir.setReturnValue(true);
            }
        }
    }

}

package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.item.StarColourHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void aries$oldStarColours(CallbackInfoReturnable<Component> cir) {
        if (!AriesFeatures.OLD_MASTER_STAR_COLOURS.enabled.get()) {
            return;
        }

        Component original = cir.getReturnValue();

        cir.setReturnValue(StarColourHandler.modify(original));
    }

}

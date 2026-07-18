package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.accessor.ItemEntityRenderStateAccessor;
import dev.voidedaries.aries.client.item.ItemRarity;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntityRenderState.class)
public class ItemEntityRenderStateMixin implements ItemEntityRenderStateAccessor {

    @Unique
    private ItemRarity aries$rarity = ItemRarity.UNKNOWN;

    @Override
    public void aries$setRarity(ItemRarity rarity) {
        this.aries$rarity = rarity;
    }

    @Override
    public ItemRarity aries$getRarity() {
        return this.aries$rarity;
    }
}

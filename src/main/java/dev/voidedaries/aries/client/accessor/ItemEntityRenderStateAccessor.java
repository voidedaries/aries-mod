package dev.voidedaries.aries.client.accessor;

import dev.voidedaries.aries.skyblock.item.ItemRarity;

public interface ItemEntityRenderStateAccessor {
    void aries$setRarity(ItemRarity rarity);
    ItemRarity aries$getRarity();
}

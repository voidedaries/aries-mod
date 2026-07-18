package dev.voidedaries.aries.client.accessor;

import dev.voidedaries.aries.client.item.ItemRarity;

public interface ItemEntityRenderStateAccessor {
    void aries$setRarity(ItemRarity rarity);
    ItemRarity aries$getRarity();
}

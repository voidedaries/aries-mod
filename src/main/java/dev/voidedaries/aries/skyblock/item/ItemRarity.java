package dev.voidedaries.aries.skyblock.item;

import dev.voidedaries.aries.client.feature.AriesFeatures;

import java.util.Locale;

public enum ItemRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC,
    DIVINE,
    SPECIAL,
    VERY_SPECIAL,
    ULTIMATE,
    ADMIN,
    UNKNOWN;

    public static ItemRarity fromText(String itemRarity) {
        String t = itemRarity.toUpperCase(Locale.ROOT);

        for (ItemRarity rarity : values()) {
            if (t.contains(rarity.name())) {
                return rarity;
            }
        }

        return UNKNOWN;
    }

    public static float getScale(ItemRarity rarity) {
        return AriesFeatures.ITEM_RARITY_SCALING.getScale(rarity);
    }
}

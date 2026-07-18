package dev.voidedaries.aries.client.item;

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
}

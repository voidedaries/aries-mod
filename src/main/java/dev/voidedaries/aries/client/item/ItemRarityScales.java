package dev.voidedaries.aries.client.item;

import dev.voidedaries.aries.client.feature.AriesFeatures;

import java.util.EnumMap;
import java.util.Map;

public final class ItemRarityScales {

    private static final Map<ItemRarity, Float> SCALES = new EnumMap<>(ItemRarity.class);

    static {
        SCALES.put(ItemRarity.COMMON, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.COMMON));
        SCALES.put(ItemRarity.UNCOMMON, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.UNCOMMON));
        SCALES.put(ItemRarity.RARE, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.RARE));
        SCALES.put(ItemRarity.EPIC, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.EPIC));
        SCALES.put(ItemRarity.LEGENDARY, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.LEGENDARY));
        SCALES.put(ItemRarity.MYTHIC, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.MYTHIC));
        SCALES.put(ItemRarity.DIVINE, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.DIVINE));

        SCALES.put(ItemRarity.SPECIAL, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.SPECIAL));
        SCALES.put(ItemRarity.VERY_SPECIAL, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.VERY_SPECIAL));
        SCALES.put(ItemRarity.ULTIMATE, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.ULTIMATE));
        SCALES.put(ItemRarity.ADMIN, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.ADMIN));
        SCALES.put(ItemRarity.UNKNOWN, AriesFeatures.ITEM_RARITY_SCALING.getScale(ItemRarity.UNKNOWN));
    }

    public static float get(ItemRarity rarity) {
        return SCALES.getOrDefault(rarity, 1.0f);
    }
}

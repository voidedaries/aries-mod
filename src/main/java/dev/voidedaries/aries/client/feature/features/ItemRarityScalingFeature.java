package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.config.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.config.BooleanConfig;
import dev.voidedaries.aries.client.feature.config.FloatConfig;
import dev.voidedaries.aries.client.item.ItemRarity;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Locale;

public class ItemRarityScalingFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("item_rarity_scaling.enabled", true)
    );

    private final EnumMap<ItemRarity, FloatConfig> rarityScales = new EnumMap<>(ItemRarity.class);

    public ItemRarityScalingFeature() {
        super(
            Component.translatable("gui.category.visuals.item_rarity_scaling.name"),
            Component.translatable("gui.category.visuals.item_rarity_scaling.description"),
            AriesCategory.VISUALS
        );

        for (ItemRarity rarity : ItemRarity.values()) {
            if (rarity == ItemRarity.UNKNOWN) {
                continue;
            }

            FloatConfig scale = new FloatConfig(
                "item_rarity_scaling." + rarity.name().toLowerCase(Locale.ROOT),
                getDefault(rarity),
                1.0f,
                20.0f,
                0.5f
            );

            scale.visibleWhen(enabled::get);

            rarityScales.put(rarity, scale);

            addEntry(
                Component.translatable(
                    "gui.category.visuals.item_rarity_" +
                        rarity.name().toLowerCase(Locale.ROOT) +
                        ".name"
                ),

                Component.translatable(
                    "gui.category.visuals.item_rarity_" +
                        rarity.name().toLowerCase(Locale.ROOT) +
                        ".description"
                ),

                scale
            ).visibleWhen(enabled::get);
        }
    }

    public float getScale(ItemRarity rarity) {
        return rarityScales.getOrDefault(rarity, rarityScales.get(ItemRarity.COMMON)).get();
    }

    private float getDefault(ItemRarity rarity) {
        return switch (rarity) {
            case UNKNOWN, COMMON -> 1.0f;
            case UNCOMMON -> 1.5f;
            case RARE -> 2.4f;
            case EPIC -> 3.2f;
            case LEGENDARY -> 5f;
            case MYTHIC -> 5.5f;
            case DIVINE -> 6f;

            case SPECIAL -> 6.5f;
            case VERY_SPECIAL -> 8.6f;
            case ULTIMATE, ADMIN -> 100.0f;
        };
    }
}

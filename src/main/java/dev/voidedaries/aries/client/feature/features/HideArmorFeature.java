package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;

public class HideArmorFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("hide_armor.enabled", true)
    );

    public final BooleanConfig hideHelmet = new BooleanConfig("hide_armor.helmet", false);
    public final BooleanConfig hideChestplate = new BooleanConfig("hide_armor.chestplate", false);
    public final BooleanConfig hideLeggings = new BooleanConfig("hide_armor.leggings", false);
    public final BooleanConfig hideBoots = new BooleanConfig("hide_armor.boots", false);

    public HideArmorFeature() {
        super(
            Component.translatable("gui.category.visuals.hide_armor.name"),
            Component.translatable("gui.category.visuals.hide_armor.description"),
            AriesCategory.VISUALS
        );

        add("helmet", hideHelmet);
        add("chestplate", hideChestplate);
        add("leggings", hideLeggings);
        add("boots", hideBoots);
    }

    private void add(String id, AriesConfigType<?> config) {
        addEntry(
            Component.translatable("gui.category.visuals.hide_armor." + id + ".name"),
            Component.translatable("gui.category.visuals.hide_armor." + id + ".description"),
            config
        ).visibleWhen(enabled::get);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public boolean isHidden(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> hideHelmet.get();
            case CHEST -> hideChestplate.get();
            case LEGS -> hideLeggings.get();
            case FEET -> hideBoots.get();
            default -> false;
        };
    }

    public void toggle(EquipmentSlot slot) {
        switch (slot) {
            case HEAD -> hideHelmet.set(!hideHelmet.get());
            case CHEST -> hideChestplate.set(!hideChestplate.get());
            case LEGS -> hideLeggings.set(!hideLeggings.get());
            case FEET -> hideBoots.set(!hideBoots.get());
        }
    }

}

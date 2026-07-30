package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.EnumSet;
import java.util.Set;

public class HideArmorFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("hide_armor.enabled", true)
    );

    private final Set<EquipmentSlot> hiddenSlots = EnumSet.noneOf(EquipmentSlot.class);

    public HideArmorFeature() {
        super(
            Component.translatable("gui.category.visuals.hide_armor.name"),
            Component.translatable("gui.category.visuals.hide_armor.description"),
            AriesCategory.VISUALS
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public boolean isHidden(EquipmentSlot slot) {
        return hiddenSlots.contains(slot);
    }

    public void toggle(EquipmentSlot slot) {
        if (hiddenSlots.contains(slot)) {
            hiddenSlots.remove(slot);
        } else {
            hiddenSlots.add(slot);
        }
    }

}

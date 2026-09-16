package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.*;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class AshfangMobsColorFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("ashfang_mob_color.enabled", true)
    );

    public enum AshfangMobType {
        FOLLOWER,
        ACOLYTE,
        UNDERLING
    }

    public final ColorConfig follower = new ColorConfig("ashfang_mob_color.follower", 0xFFA8BFD2);

    public final ColorConfig acolyte = new ColorConfig("ashfang_mob_color.acolyte", 0xFF459BFF);

    public final ColorConfig underling = new ColorConfig("ashfang_mob_color.underling", 0xFFFF5555);

    public AshfangMobsColorFeature() {
        super(
            Component.translatable("gui.category.crimson_isle.ashfang_mob_color.name"),
            Component.translatable("gui.category.crimson_isle.ashfang_mob_color.description"),
            AriesCategory.CRIMSON_ISLE
        );

        add(AshfangMobType.FOLLOWER.toString().toLowerCase(Locale.ROOT), follower);
        add(AshfangMobType.ACOLYTE.toString().toLowerCase(Locale.ROOT), acolyte);
        add(AshfangMobType.UNDERLING.toString().toLowerCase(Locale.ROOT), underling);
    }

    private void add(String id, AriesConfigType<?> config) {
        addEntry(
            Component.translatable("gui.category.crimson_isle.ashfang_mob_color." + id + ".name"),
            Component.translatable("gui.category.crimson_isle.ashfang_mob_color." + id + ".description"),
            config
        ).visibleWhen(enabled::get);
    }

}

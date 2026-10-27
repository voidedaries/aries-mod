package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class PersistentServerResourcePackFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("persistent_resource_pack.enabled", true)
    );

    public PersistentServerResourcePackFeature() {
        super(
            Component.translatable("gui.category.misc.persistent_resource_pack.name"),
            Component.translatable("gui.category.misc.persistent_resource_pack.description"),
            AriesCategory.MISC
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

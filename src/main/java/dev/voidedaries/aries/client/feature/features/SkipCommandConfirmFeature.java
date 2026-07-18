package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import net.minecraft.network.chat.Component;

public class SkipCommandConfirmFeature extends AriesFeature {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("compact_chat.enabled", true)
    );

    public SkipCommandConfirmFeature() {
        super(
            Component.translatable("gui.category.commands.skip_command_confirmation.name"),
            Component.translatable("gui.category.commands.skip_command_confirmation.description"),
            AriesCategory.COMMANDS
        );
    }

    public boolean isEnabled() {
        return enabled.get();
    }

}

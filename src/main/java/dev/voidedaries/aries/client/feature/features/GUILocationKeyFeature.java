package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.KeybindConfig;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class GUILocationKeyFeature extends AriesFeature {
    public final KeybindConfig keybind = addConfig(new KeybindConfig("gui_location.keybind", GLFW.GLFW_KEY_UNKNOWN));

    public GUILocationKeyFeature() {
        super(
            Component.translatable("gui.category.settings.gui_location_keybind.name"),
            Component.translatable("gui.category.settings.gui_location_keybind.description"),
            AriesCategory.SETTINGS
        );
    }

    public boolean isDown() {
        return keybind.isDown();
    }
}

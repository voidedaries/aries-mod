package dev.voidedaries.aries.client.keybind;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.types.KeybindConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class KeybindManager {

    public static void init() {
        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            for (AriesConfigType<?> config : feature.getConfigs()) {
                if (config instanceof KeybindConfig keybind) {
                    keybind.register();
                }
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register(KeybindManager::onClientTick);
    }

    private static void onClientTick(Minecraft minecraft) {
        //? if 26.2
        //Screen screen = minecraft.gui.screen();
        //? if 26.1.2
        Screen screen = minecraft.screen;

        if (screen != null) {
            return;
        }

        if (AriesFeatures.GUI_LOCATION_KEY.keybind.isDown()) {
            AriesFeatures.GUI_LOCATION_BUTTON.openGUILocationEditor();
        }
    }

}

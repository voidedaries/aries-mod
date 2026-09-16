package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.ButtonConfig;
import dev.voidedaries.aries.client.gui.location.GUILocationsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GUILocationButtonFeature extends AriesFeature {

    public GUILocationButtonFeature() {
        super(
            Component.translatable("gui.category.settings.gui_location_button.name"),
            Component.translatable("gui.category.settings.gui_location_button.description"),
            AriesCategory.SETTINGS
        );

        addConfig(
            new ButtonConfig(Component.translatable("gui.category.settings.gui_location.button"), this::openGUILocationEditor)
        );
    }

    public void openGUILocationEditor() {
        Minecraft minecraft = Minecraft.getInstance();

        //? if 26.2 {
        /*minecraft.execute(() -> minecraft.gui.setScreen(new GUILocationsScreen(minecraft.gui.screen())));
        *///?}
        //? if 26.1.2 {
        minecraft.execute(() -> minecraft.setScreen(new GUILocationsScreen(minecraft.screen)));
        //?}
    }

}

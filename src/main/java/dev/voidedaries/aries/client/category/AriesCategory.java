package dev.voidedaries.aries.client.category;

import net.minecraft.network.chat.Component;

public enum AriesCategory {

    ABOUT("gui.menu.category.about"),
    SETTINGS("gui.menu.category.settings"),
    ACHIEVEMENTS("gui.menu.category.achievements"),
    CHAT("gui.menu.category.chat"),
    FISHING("gui.menu.category.fishing"),
    VISUALS("gui.menu.category.visuals"),
    PERFORMANCE("gui.menu.category.performance"),
    CONTROLS("gui.menu.category.controls"),
    DEV("gui.menu.category.dev");

    private final String translationKey;

    AriesCategory(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getName() {
        return Component.translatable(translationKey);
    }

    public String getTranslationKey() {
        return translationKey;
    }

}

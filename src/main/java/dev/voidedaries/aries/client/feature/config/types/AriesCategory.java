package dev.voidedaries.aries.client.feature.config.types;

import net.minecraft.network.chat.Component;

public enum AriesCategory {

    ABOUT("about"),
    SETTINGS("settings"),
    ACHIEVEMENTS("achievements"),
    CHAT("chat"),
    COMMANDS("commands"),
    FISHING("fishing"),
    VISUALS("visuals"),
    PERFORMANCE("performance"),
    CONTROLS("controls"),
    DEV("dev");

    private final String id;

    AriesCategory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return Component.translatable("gui.menu.category." + id);
    }

}

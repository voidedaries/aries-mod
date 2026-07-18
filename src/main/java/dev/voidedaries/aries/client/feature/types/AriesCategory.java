package dev.voidedaries.aries.client.feature.types;

import net.minecraft.network.chat.Component;

/**
 * Represents a category used to group Aries features within the configuration menu.
 *
 * <p>Each category has a unique identifier used for translation keys and
 * a display name retrieved through Minecraft's translation system.</p>
 *
 * @see dev.voidedaries.aries.client.feature.AriesFeature
 */
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

    /**
     * Gets the internal identifier of this category.
     *
     * @return category identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the translated display name of this category.
     *
     * @return translated category name
     */
    public Component getName() {
        return Component.translatable("gui.menu.category." + id);
    }

}

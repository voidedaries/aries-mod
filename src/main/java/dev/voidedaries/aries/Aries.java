package dev.voidedaries.aries;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class Aries implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public void onInitialize() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("aries", path);
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void log(String message, Object... params) {
        LOGGER.info(message, params);
    }

    public static void debug(String message) {
        LOGGER.debug(message);
    }

    public static void debug(String message, Object... params) {
        LOGGER.debug(message, params);
    }
}

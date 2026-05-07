package dev.voidedaries.aries.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import dev.voidedaries.aries.Aries;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class AriesConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path ARIES_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("aries").resolve("aries_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final AriesConfig INSTANCE = load();

    public static void init() {
        Aries.log("Loading Aries config...");
    }

    public static void save() {
        try {
            Files.createDirectories(ARIES_FILE.getParent());

            try (Writer writer = Files.newBufferedWriter(ARIES_FILE)) {
                GSON.toJson(INSTANCE, writer);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to save parallax options file", e);
        }
    }

    private static AriesConfig load() {
        try (Reader reader = Files.newBufferedReader(ARIES_FILE)) {
            AriesConfig options = GSON.fromJson(reader, AriesConfig.class);

            return options == null ? new AriesConfig() : options;
        } catch (NoSuchFileException e) {
            return createDefaultConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to load parallax options file", e);
            return new AriesConfig();
        }

    }

    private static AriesConfig createDefaultConfig() {
        AriesConfig config = new AriesConfig();

        try {
            Files.createDirectories(ARIES_FILE.getParent());

            try (Writer writer = Files.newBufferedWriter(ARIES_FILE)) {
                GSON.toJson(config, writer);
            }

            Aries.log("Created default Aries config!");

        } catch (IOException e) {
            LOGGER.error("Failed to create default config file", e);
        }

        return config;
    }
}

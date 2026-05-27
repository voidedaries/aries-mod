package dev.voidedaries.aries.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.config.AriesConfigType;
import dev.voidedaries.aries.client.feature.config.BooleanConfig;
import dev.voidedaries.aries.client.feature.config.IntConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AriesConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Path ARIES_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("aries").resolve("aries_config.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static AriesConfig INSTANCE;

    public Map<String, JsonElement> values = new HashMap<>();

    public static void init() {
        Aries.log("Loading Aries config...");

        INSTANCE = load();
        save();
    }

    public static void save() {
        if (INSTANCE == null) {
            LOGGER.warn("Tried to save config before init");
            return;
        }

        try {
            Files.createDirectories(ARIES_FILE.getParent());

            syncRuntimeToConfig();

            try (Writer writer = Files.newBufferedWriter(ARIES_FILE)) {
                GSON.toJson(INSTANCE.values, writer);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to save aries config file", e);
        }
    }

    private static AriesConfig load() {
        try (Reader reader = Files.newBufferedReader(ARIES_FILE)) {
            AriesConfig config = GSON.fromJson(reader, AriesConfig.class);

            if (config == null) {
                config = new AriesConfig();
            }

            fillMissingDefaults(config);
            applyToRuntimeConfig(config);

            return config;
        } catch (NoSuchFileException e) {
            //creates and applies defaults
            return createDefaultConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to load aries config file", e);

            AriesConfig fallback = new AriesConfig();
            fillMissingDefaults(fallback);
            applyToRuntimeConfig(fallback);

            return fallback;
        }

    }

    private static void applyToRuntimeConfig(AriesConfig config) {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            JsonElement element = config.values.get(option.getKey());

            // if missing in file, insert default immediately
            if (element == null) {
                continue;
            }

            if (option instanceof BooleanConfig bool) {
                bool.set(element.getAsBoolean());
            }

            else if (option instanceof IntConfig integer) {
                integer.set(element.getAsInt());
            }
        }
    }

    private static void syncRuntimeToConfig() {
        if (INSTANCE == null) {
            return;
        }

        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            String key = option.getKey();

            if (option instanceof BooleanConfig bool) {
                INSTANCE.values.put(key, new JsonPrimitive(bool.get()));
            }
            else if (option instanceof IntConfig integer) {
                INSTANCE.values.put(key, new JsonPrimitive(integer.get()));
            }
        }
    }

    private static void fillMissingDefaults(AriesConfig config) {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            config.values.putIfAbsent(option.getKey(), toJson(option));
        }
    }

    private static AriesConfig createDefaultConfig() {
        AriesConfig config = new AriesConfig();

        fillMissingDefaults(config);
        saveToFile(config);
        applyToRuntimeConfig(config);

        Aries.log("Created default aries config");
        return config;
    }

    private static void saveToFile(AriesConfig config) {
        try {
            Files.createDirectories(ARIES_FILE.getParent());

            try (Writer writer = Files.newBufferedWriter(ARIES_FILE)) {
                GSON.toJson(config.values, writer);
            }

            Aries.log("Created default Aries config!");

        } catch (IOException e) {
            LOGGER.error("Failed to create default config file", e);
        }
    }

    private static JsonPrimitive toJson(AriesConfigType<?> option) {
        if (option instanceof BooleanConfig b) {
            return new JsonPrimitive(b.getDefaultValue());
        }

        if (option instanceof IntConfig i) {
            return new JsonPrimitive(i.getDefaultValue());
        }

        throw new IllegalStateException("Unsupported config type: " + option.getClass());
    }
}

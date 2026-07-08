package dev.voidedaries.aries.client;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.config.types.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AriesConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Path ARIES_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("aries").resolve("config.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static AriesConfig INSTANCE;

    public Map<String, JsonObject> categories = new HashMap<>();

    public static void init() {
        Aries.log("Loading Aries config...");
        INSTANCE = load();
        save();
    }

    public static void save() {
        if (INSTANCE == null) {
            return;
        }

        try {
            ConfigWatcher.setIgnore(true);
            Files.createDirectories(ARIES_FILE.getParent());
            writeFromRuntime();

            try (Writer writer = Files.newBufferedWriter(ARIES_FILE)) {
                GSON.toJson(INSTANCE.categories, writer);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to save aries config file", e);
        } finally {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}

                ConfigWatcher.setIgnore(false);
            }).start();
        }
    }

    private static void writeFromRuntime() {
        INSTANCE.categories.clear();

        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            if (option.getFeature() == null) {
                continue;
            }

            String category = categoryOf(option);
            JsonObject obj = INSTANCE.categories.computeIfAbsent(category, _ -> new JsonObject());
            obj.add(option.getKey(), write(option));
        }
    }

    public static AriesConfig load() {
        try (Reader reader = Files.newBufferedReader(ARIES_FILE)) {
            Map<String, JsonObject> data = GSON.fromJson(
                reader, new TypeToken<Map<String, JsonObject>>(){}.getType()
            );

            AriesConfig config = new AriesConfig();

            if (data != null) {
                config.categories.putAll(data);
            }

            applyOptionToConfig(config);

            return config;
        } catch (NoSuchFileException e) {
            return createDefaultConfig();
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Failed to load config file or is malformed", e);

            AriesConfig fallback = new AriesConfig();
            fillMissingDefaults(fallback);
            applyOptionToConfig(fallback);
            return fallback;
        }

    }

    static void applyOptionToConfig(AriesConfig config) {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            if (option.getFeature() == null) {
                continue;
            }

            JsonObject obj =
                config.categories.computeIfAbsent(categoryOf(option), _ -> new JsonObject());

            JsonElement element = obj.get(option.getKey());

            if (element == null) {
                continue;
            }

            //noinspection IfCanBeSwitch
            if (option instanceof BooleanConfig b) {
                b.set(element.getAsBoolean());
            }
            else if (option instanceof IntConfig i) {
                i.set(element.getAsInt());
            }
            else if (option instanceof FloatConfig f) {
                f.set(element.getAsFloat());
            }
            else if (option instanceof ColorConfig c) {
                try {
                    String raw = element.getAsString().trim();
                    c.set(ColorConfig.parse(raw));
                } catch (Exception e) {
                    LOGGER.warn("Invalid color in config for {}: {}", option.getKey(), element);
                }
            }
        }
    }

    private static void fillMissingDefaults(AriesConfig config) {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {

            if (option.getFeature() == null) {
                continue;
            }

            String category = categoryOf(option);
            JsonObject obj = config.categories.computeIfAbsent(category, _ -> new JsonObject());

            if (!obj.has(option.getKey())) {
                obj.add(option.getKey(), defaultJson(option));
            }
        }
    }

    private static AriesConfig createDefaultConfig() {
        AriesConfig config = new AriesConfig();

        fillMissingDefaults(config);
        INSTANCE = config;

        applyOptionToConfig(config);

        Aries.log("Created default aries config");
        return config;
    }

    private static JsonElement write(AriesConfigType<?> option) {
        if (option instanceof BooleanConfig b) {
            return new JsonPrimitive(b.get());
        }

        if (option instanceof IntConfig i) {
            return new JsonPrimitive(i.get());
        }

        if (option instanceof FloatConfig f) {
            return new JsonPrimitive(f.get());
        }

        if (option instanceof ColorConfig c) {
            return new JsonPrimitive(String.format("0x%08X", c.get()));
        }

        throw new IllegalStateException("Unsupported config type: " + option.getClass());
    }

    private static JsonElement defaultJson(AriesConfigType<?> option) {
        if (option instanceof BooleanConfig b) {
            return new JsonPrimitive(b.getDefaultValue());
        }

        if (option instanceof IntConfig i) {
            return new JsonPrimitive(i.getDefaultValue());
        }

        if (option instanceof FloatConfig f) {
            return new JsonPrimitive(f.getDefaultValue());
        }

        if (option instanceof ColorConfig c) {
            return new JsonPrimitive(String.format("0x%08X", c.getDefaultValue()));
        }

        throw new IllegalStateException("Unsupported type: " + option.getClass());
    }

    private static String categoryOf(AriesConfigType<?> option) {
        return option.getFeature().getCategory().name().toLowerCase(Locale.ROOT);
    }

    public static void resetToDefaults() {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {

            if (option instanceof BooleanConfig b) {
                b.set(b.getDefaultValue());
            } else if (option instanceof IntConfig i) {
                i.set(i.getDefaultValue());
            } else if (option instanceof FloatConfig f) {
                f.set(f.getDefaultValue());
            } else if (option instanceof ColorConfig c) {
                c.set(c.getDefaultValue());
            }
        }

        save();
    }

    public static Path getConfigFile() {
        return ARIES_FILE;
    }

    public static void reload() {
        INSTANCE = load();
    }
}

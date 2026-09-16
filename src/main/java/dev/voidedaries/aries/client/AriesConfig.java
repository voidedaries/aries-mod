package dev.voidedaries.aries.client;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.types.*;
import dev.voidedaries.aries.client.gui.location.AriesHudManager;
import dev.voidedaries.aries.client.gui.location.HudPosition;
import dev.voidedaries.aries.client.gui.location.HudRenderable;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
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
import java.util.Objects;

public class AriesConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Path ARIES_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("aries").resolve("config.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static AriesConfig INSTANCE;

    public Map<String, JsonObject> categories = new HashMap<>();
    private static final String HUD_CATEGORY = "hud";

    private String lastVersion = "";

    private static boolean newVersion;

    public static boolean isNewVersion() {
        return newVersion;
    }

    private static String getAriesVersion(String fullVersion) {
        int plusIndex = fullVersion.indexOf('+');

        if (plusIndex == -1 || plusIndex == fullVersion.length() - 1) {
            return fullVersion;
        }

        return fullVersion.substring(plusIndex + 1);
    }

    public static void init() {
        Aries.log("Loading Aries config...");
        INSTANCE = load();

        String currentVersion = Objects.requireNonNull(ModConstants.version);

        newVersion = false;

        if (INSTANCE.lastVersion.isEmpty()) {
            newVersion = true;
        } else {
            try {
                String savedModVersion = getAriesVersion(INSTANCE.lastVersion);
                String currentModVersion = getAriesVersion(currentVersion);

                newVersion = Version.parse(savedModVersion).compareTo(Version.parse(currentModVersion)) < 0;
            } catch (VersionParsingException e) {
                LOGGER.warn("Invalid saved Aries version '{}'", INSTANCE.lastVersion, e);
            }
        }

        INSTANCE.lastVersion = currentVersion;

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

            JsonObject root = new JsonObject();

            for (Map.Entry<String, JsonObject> entry : INSTANCE.categories.entrySet()) {
                root.add(entry.getKey(), entry.getValue());
            }

            root.addProperty("lastVersion", INSTANCE.lastVersion);

            try (Writer writer = Files.newBufferedWriter(ARIES_FILE)) {
                GSON.toJson(root, writer);
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

    public static AriesConfig load() {
        try (Reader reader = Files.newBufferedReader(ARIES_FILE)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);

            AriesConfig config = new AriesConfig();

            if (root != null) {
                JsonElement version = root.remove("lastVersion");

                if (version != null && version.isJsonPrimitive()) {
                    config.lastVersion = version.getAsString();
                }

                for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                    if (entry.getValue().isJsonObject()) {
                        config.categories.put(entry.getKey(), entry.getValue().getAsJsonObject());
                    }
                }
            }

            applyOptionToConfig(config);
            applyHudPositions(config);

            return config;
        } catch (NoSuchFileException e) {
            return createDefaultConfig();
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Failed to load config file or is malformed", e);

            AriesConfig fallback = new AriesConfig();
            fillMissingDefaults(fallback);

            applyOptionToConfig(fallback);
            applyHudPositions(fallback);
            return fallback;
        }

    }

    private static void writeFromRuntime() {
        INSTANCE.categories.clear();

        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            if (option.getFeature() == null || option instanceof ButtonConfig) {
                continue;
            }

            String category = categoryOf(option);
            JsonObject obj = INSTANCE.categories.computeIfAbsent(category, _ -> new JsonObject());
            obj.add(option.getKey(), write(option));
        }

        writeHudPositions();
    }

    static void applyOptionToConfig(AriesConfig config) {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            if (option.getFeature() == null || option instanceof ButtonConfig) {
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
            else if (option instanceof KeybindConfig k) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();

                    if (primitive.isString()) {
                        k.set(parseKeybind(primitive.getAsString()));
                    } else if (primitive.isNumber()) {
                        k.set(primitive.getAsInt());
                    }
                }
            }
            else if (option instanceof ColorConfig c) {
                try {
                    String raw = element.getAsString().trim();
                    c.set(ColorConfig.parse(raw));
                } catch (Exception e) {
                    LOGGER.warn("Invalid color in config for {}: {}", option.getKey(), element);
                }
            } else if (option instanceof ListConfig<?> list) {
                try {
                    list.deserialize(element.getAsString());
                } catch (Exception e) {
                    LOGGER.warn("Invalid list value in config for {}: {}", option.getKey(), element);
                }
            }
        }
    }

    private static void applyHudPositions(AriesConfig config) {
        JsonObject hud = config.categories.get(HUD_CATEGORY);

        if (hud == null) {
            return;
        }

        for (HudRenderable renderable : AriesHudManager.getHudElements()) {
            JsonElement element = hud.get(renderable.getHudId());

            if (element == null || !element.isJsonObject()) {
                continue;
            }

            JsonObject positionObject = element.getAsJsonObject();

            int x = positionObject.has("x") ? positionObject.get("x").getAsInt() : 0;
            int y = positionObject.has("y") ? positionObject.get("y").getAsInt() : 0;

            renderable.getHudPosition().setPosition(x, y);
        }
    }

    private static void fillMissingDefaults(AriesConfig config) {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {

            if (option.getFeature() == null || option instanceof ButtonConfig) {
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
        applyHudPositions(config);

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

        if (option instanceof KeybindConfig k) {
            return new JsonPrimitive(k.getCurrentKey().getName());
        }

        if (option instanceof ColorConfig c) {
            return new JsonPrimitive(String.format("0x%08X", c.get()));
        }

        if (option instanceof ListConfig<?> l) {
            return new JsonPrimitive(l.getValueName());
        }

        throw new IllegalStateException("Unsupported config type: " + option.getClass());
    }

    private static void writeHudPositions() {
        JsonObject hud = new JsonObject();

        for (HudRenderable renderable : AriesHudManager.getHudElements()) {
            HudPosition position = renderable.getHudPosition();

            JsonObject positionObject = new JsonObject();

            positionObject.addProperty("x", position.getX());
            positionObject.addProperty("y", position.getY());

            hud.add(renderable.getHudId(), positionObject);
        }

        INSTANCE.categories.put(HUD_CATEGORY, hud);
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

        if (option instanceof KeybindConfig k) {
            return new JsonPrimitive(InputConstants.Type.KEYSYM.getOrCreate(k.getDefaultValue()).getName());
        }

        if (option instanceof ColorConfig c) {
            return new JsonPrimitive(String.format("0x%08X", c.getDefaultValue()));
        }

        if (option instanceof ListConfig<?> l) {
            Object value = l.getDefaultValue();

            if (value instanceof Enum<?> enumValue) {
                return new JsonPrimitive(enumValue.name());
            }

            throw new IllegalStateException("Unsupported ListConfig value: " + value.getClass());
        }

        throw new IllegalStateException("Unsupported type: " + option.getClass());
    }

    private static String categoryOf(AriesConfigType<?> option) {
        return option.getFeature().getCategory().name().toLowerCase(Locale.ROOT);
    }

    public static void resetToDefaults() {
        for (AriesConfigType<?> option : AriesFeatures.getAllConfigs()) {
            if (option instanceof ButtonConfig) {
                continue;
            }

            option.reset();
        }

        for (HudRenderable renderable : AriesHudManager.getHudElements()) {
            renderable.getHudPosition().setPosition(0, 0);
        }

        save();
    }

    private static int parseKeybind(String key) {
        try {
            return InputConstants.getKey(key).getValue();
        } catch (Exception e) {
            Aries.log("Failed to parse keybind: " + key, e);
            return InputConstants.UNKNOWN.getValue();
        }
    }

    public static Path getConfigFile() {
        return ARIES_FILE;
    }

    public static void reload() {
        INSTANCE = load();
    }
}

package dev.voidedaries.aries.client.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.message.AriesMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;

public class AriesUpdateChecker {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private AriesUpdateChecker() {}

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((_, _, client) -> {
            if (!client.hasSingleplayerServer()) {
                check();
            }
        });
    }

    public static void check() {
        Thread.startVirtualThread(() -> {
            try {
                String url =
                    "https://api.modrinth.com/v2/project/"
                        + ModConstants.MOD_ID
                        + "/version"
                        + "?loaders=%5B%22fabric%22%5D"
                        + "&game_versions=%5B%22"
                        + ModConstants.minecraftVersion
                        + "%22%5D"
                        + "&include_changelog=false";

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "VoidedAries/Aries/" + ModConstants.VERSION).GET().build();

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                    Aries.warn("Failed to check for updates: HTTP {}", response.statusCode());
                    return;
                }

                ModrinthVersion latestVersion = getLatestRelease(response.body());

                if (latestVersion == null) {
                    return;
                }

                if (!isNewerVersion(latestVersion.version())) {
                    return;
                }

                Minecraft.getInstance().execute(() -> showUpdateMessage(latestVersion));
            } catch (IOException | InterruptedException e) {
                Aries.warn("Failed to check for updates: {}", e.getMessage());
            }
        });
    }

    private static Version parseVersion(String version) {
        String[] parts = version.split("\\.");

        int major = parts.length > 0 ? parsePart(parts[0]) : 0;
        int minor = parts.length > 1 ? parsePart(parts[1]) : 0;
        int patch = parts.length > 2 ? parsePart(parts[2]) : 0;

        return new Version(major, minor, patch);
    }

    private static ModrinthVersion getLatestRelease(String response) {
        JsonArray versions = JsonParser.parseString(response).getAsJsonArray();

        return versions.asList().stream()
            .filter(JsonElement::isJsonObject)
            .map(JsonElement::getAsJsonObject)
            .filter(version -> version.get("version_type").getAsString().equals("release"))
            .map(version ->
                new ModrinthVersion(version.get("version_number").getAsString(), version.get("id").getAsString())
            )
            .max(Comparator.comparing(version -> parseVersion(version.version())))
            .orElse(null);
    }

    private static int parsePart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("[^0-9].*", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean isNewerVersion(String latestVersion) {
        return parseVersion(latestVersion).compareTo(parseVersion(ModConstants.VERSION)) > 0;
    }

    private static void showUpdateMessage(ModrinthVersion latestVersion) {
        Component message = Component.literal("A new update is available: ")
            .append(Component.literal(latestVersion.version()).withStyle(ChatFormatting.BLUE))
            .append(Component.literal(" "))
            .append(
                Component.literal("Click here")
                    .withStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withUnderlined(true)
                            .withColor(ChatFormatting.AQUA)
                            .withClickEvent(
                                new ClickEvent.OpenUrl(
                                    URI.create(
                                        "https://modrinth.com/mod/"
                                            + ModConstants.MOD_ID
                                            + "/version/"
                                            + latestVersion.id()
                                    )
                                )
                            )
                    )
            ).append(Component.literal(" to go to the download page"));

        AriesMessage.sendPlayerInfoMessage(message);
    }

    private record ModrinthVersion(String version, String id) {}

    private record Version(int major, int minor, int patch) implements Comparable<Version> {
        @Override
        public int compareTo(Version other) {
            int comparison = Integer.compare(major, other.major);

            if (comparison != 0) {
                return comparison;
            }

            comparison = Integer.compare(minor, other.minor);

            if (comparison != 0) {
                return comparison;
            }

            return Integer.compare(patch, other.patch);
        }
    }

}

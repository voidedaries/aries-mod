package dev.voidedaries.aries.skyblock.repo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.skyblock.NeuItemJsonParser;
import dev.voidedaries.aries.skyblock.SkyblockItemLookup;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkyBlockRepoDownloader {
    private static final Path REPO_DIRECTORY = FabricLoader.getInstance().getConfigDir()
        .resolve(ModConstants.MOD_ID.toLowerCase(Locale.ROOT))
        .resolve("neu-skyblock-repo");

    // for sha key
    private static final Path COMMIT_FILE = REPO_DIRECTORY.resolve("version");

    public static final Path NEU_REPO_ZIP = REPO_DIRECTORY.resolve("neu-skyblock-repo.download.zip");

    private static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Aries-NEU-Repo");
        thread.setDaemon(true);
        return thread;
    });

    // Runs the repository asynchronously on a dedicated executor so repository/network operations never block the client thread.
    public static void updateAsync() {
        EXECUTOR.submit(SkyBlockRepoDownloader::update);
    }

    private static void update() {
        // checks GitHub for the latest commit SHA of the NEU item repository.
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ModConstants.NEU_API))
                .header("Accept", "application/vnd.github+json")
                .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            String latestCommit = json.get("sha").getAsString();
            String localCommit = getLocalCommit();

            Aries.log("Latest NEU repo commit: {}", latestCommit);
            Aries.log("Local NEU repo commit: {}", localCommit);

            if (latestCommit.equals(localCommit)) {
                Aries.log("NEU repository is already up to date");
                NeuItemJsonParser.readNeuSkyblockItemZip();
                return;
            }

            Aries.log("NEU repository update required");

            downloadRepo(latestCommit);
            saveLocalCommit(latestCommit);

            NeuItemJsonParser.readNeuSkyblockItemZip();
        } catch (Exception ex) {
            Aries.warn("Failed to check NEU repository: {}", ex);
        } finally {
            SkyblockItemLookup.finishInit();
        }
    }

    private static URI getRepoZipUri(String commit) {
        return URI.create("https://github.com/NotEnoughUpdates/NotEnoughUpdates-REPO/archive/" + commit + ".zip");
    }

    private static void downloadRepo(String commit) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(getRepoZipUri(commit)).build();

        Aries.log("Downloading NEU repository: {}", commit);

        Files.createDirectories(NEU_REPO_ZIP.getParent());

        long start = System.nanoTime();

        HttpResponse<Path> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofFile(NEU_REPO_ZIP));

        double elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0;

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            Files.deleteIfExists(NEU_REPO_ZIP);

            throw new IOException("Github returned HTTP " + response.statusCode());
        }

        Aries.log("NEU repository download complete in {} seconds", String.format("%.2f", elapsedSeconds));
    }

    @Nullable
    private static String getLocalCommit() {
        try {
            return Files.readString(COMMIT_FILE).trim();
        } catch (NoSuchFileException ex) {
            return null;
        } catch (IOException ex) {
            Aries.warn("Failed to read NEU repository version: {}", ex.getMessage());
            return null;
        }
    }

    private static void saveLocalCommit(String commit) {
        try {
            Files.createDirectories(REPO_DIRECTORY);
            Files.writeString(COMMIT_FILE, commit);
        } catch (IOException ex) {
            Aries.warn("Failed to save NEU repository version: {}", ex.getMessage());
        }
    }
}

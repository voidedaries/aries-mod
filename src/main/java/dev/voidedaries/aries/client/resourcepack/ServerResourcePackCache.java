package dev.voidedaries.aries.client.resourcepack;

import com.google.common.hash.HashCode;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class ServerResourcePackCache {
    public static final String CACHE_DIR = ModConstants.MOD_ID + "/resource-packs";
    private static final int BUFFER_SIZE = 8192;

    private static Path cacheDirectory;

    private ServerResourcePackCache() {}


    /**
     * Initialize the cache directory.
     * Call once during client initialization.
     */
    public static void init(Path gameDir) {
        cacheDirectory = gameDir.resolve(CACHE_DIR);

        try {
            Files.createDirectories(cacheDirectory);
            Aries.log("Resource pack cache initialized at {}", cacheDirectory);
        } catch (IOException e) {
            cacheDirectory = null;
            Aries.warn("Failed to initialize resource pack cache", e);
        }
    }

    /**
     * Cache a successfully downloaded resource pack after verifying its SHA-1.
     */
    public static void cacheDownloaded(UUID id, HashCode expectedHash, Path downloadedPath) {
        if (expectedHash == null || downloadedPath == null) {
            return;
        }

        Path target = getPackPath(id, expectedHash);

        if (target == null) {
            return;
        }

        Path temporary = null;

        try {
            if (!Files.isRegularFile(downloadedPath) || !hashFile(downloadedPath).equals(expectedHash)) {
                Aries.warn("Downloaded resource pack {} failed hash verification", id);
                return;
            }

            Files.createDirectories(target.getParent());

            if (isCached(id, expectedHash)) {
                return;
            }

            temporary = Files.createTempFile(target.getParent(), expectedHash.toString(), ".tmp");

            Files.copy(downloadedPath, temporary, StandardCopyOption.REPLACE_EXISTING);

            if (!hashFile(temporary).equals(expectedHash)) {
                Aries.warn("Copied resource pack {} failed hash verification", id);
                return;
            }

            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }

            Aries.log("Cached server resource pack {} ({})", id, expectedHash);
        } catch (IOException e) {
            Aries.warn("Failed to cache server resource pack {}", id, e);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Get the location for a resource pack identified by its UUID and SHA-1.
     */
    public static Path getPackPath(UUID id, HashCode hash) {
        if (cacheDirectory == null || id == null || hash == null) {
            return null;
        }

        return cacheDirectory.resolve(id.toString()).resolve(hash.toString());
    }

    /**
     * Check whether a cached pack exists and matches the supplied SHA-1.
     */
    public static boolean isCached(UUID id, HashCode expectedHash) {
        Path path = getPackPath(id, expectedHash);

        if (path == null || !Files.isRegularFile(path)) {
            return false;
        }

        try {
            HashCode actualHash = hashFile(path);
            if (actualHash.equals(expectedHash)) {
                return true;
            }

            Aries.warn("Cached resource pack {} failed hash verification", path);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            Aries.warn("Failed to verify cached resource pack {}", path, e);
        }

        return false;
    }

    /**
     * Calculate the SHA-1 hash of a file.
     */
    private static HashCode hashFile(Path path) throws IOException {
        final MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is unavailable", e);
        }

        try (InputStream input = Files.newInputStream(path)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        return HashCode.fromBytes(digest.digest());
    }

}

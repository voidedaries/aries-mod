package dev.voidedaries.aries.client.resourcepack;

import com.google.common.hash.HashCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.UUID;

public final class PersistentHypixelPack {
    private static Path metadataFile;
    private static volatile Entry pendingPack;
    private static volatile boolean pendingPackOffered;

    private PersistentHypixelPack() {}

    public static void init(Path gameDir) {
        metadataFile = gameDir.resolve(ServerResourcePackCache.CACHE_DIR).resolve("last-active.properties");
    }

    public static void setPendingPack(UUID id, HashCode hash, Path path) {
        pendingPackOffered = false;

        if (id == null || hash == null || path == null) {
            pendingPack = null;
            return;
        }

        if (!ServerResourcePackCache.isCached(id, hash)) {
            pendingPack = null;
            return;
        }

        Path cachedPath = ServerResourcePackCache.getPackPath(id, hash);
        pendingPack = cachedPath == null ? null : new Entry(id, hash, cachedPath);
    }

    public static void markPendingPackOffered(UUID id, HashCode hash) {
        Entry pending = pendingPack;

        if (pending != null && pending.id().equals(id) && pending.hash().equals(hash)) {
            pendingPackOffered = true;
        }
    }

    public static boolean wasPendingPackOffered() {
        return pendingPack != null && pendingPackOffered;
    }

    public static void resetPendingPackOffered() {
        pendingPackOffered = false;
    }

    public static void clearPendingPack() {
        pendingPack = null;
        pendingPackOffered = false;
    }

    public static Entry getPackForRepository() {
        Entry pending = pendingPack;
        if (pending != null && ServerResourcePackCache.isCached(pending.id(), pending.hash())) {
            return pending;
        }

        return getActivePack();
    }

    public static void markActive(UUID id, HashCode hash) {
        if (metadataFile == null || id == null || hash == null) {
            return;
        }

        Properties properties = new Properties();
        properties.setProperty("uuid", id.toString());
        properties.setProperty("sha1", hash.toString());

        Path temporary = metadataFile.resolveSibling(metadataFile.getFileName() + ".tmp");

        try {
            Files.createDirectories(metadataFile.getParent());

            try (OutputStream output = Files.newOutputStream(temporary)) {
                properties.store(output, "Aries last successfully activated pack");
            }

            try {
                Files.move(temporary, metadataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveFailure) {
                Files.move(temporary, metadataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ignored) {}
    }

    public static boolean isSameAsActive(UUID id, HashCode hash) {
        Entry active = getActivePack();

        return active != null
            && active.id().equals(id)
            && active.hash().equals(hash)
            && ServerResourcePackCache.isCached(id, hash);
    }

    public record Entry(UUID id, HashCode hash, Path path) {}

    public static Entry getActivePack() {
        if (metadataFile == null || !Files.isRegularFile(metadataFile)) {
            return null;
        }

        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(metadataFile)) {
            properties.load(input);

            UUID id = UUID.fromString(properties.getProperty("uuid"));
            HashCode hash = HashCode.fromString(properties.getProperty("sha1"));

            if (!ServerResourcePackCache.isCached(id, hash)) {
                return null;
            }

            Path path = ServerResourcePackCache.getPackPath(id, hash);
            return path == null ? null : new Entry(id, hash, path);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Entry getPendingPack() {
        return pendingPack;
    }
}

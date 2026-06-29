package dev.voidedaries.aries.client;

import com.mojang.logging.LogUtils;

import java.io.IOException;
import java.nio.file.*;

public class ConfigWatcher implements Runnable {
    private final Path configFile = AriesConfig.getConfigFile();
    private static volatile boolean ignore;

    public static boolean isIgnoringWrites() {
        return ignore;
    }

    public static void setIgnore(boolean value) {
        ignore = value;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path dir = this.configFile.getParent();
            dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

            while(!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take();

                for(WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path)event.context();

                    if (!this.configFile.getFileName().equals(changed)) {
                        continue;
                    }

                    if (ConfigWatcher.isIgnoringWrites()) {
                        continue;
                    }

                    AriesConfig.reload();
                }

                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException var9) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LogUtils.getLogger().error("Config watcher failed", e);
        }
    }
}

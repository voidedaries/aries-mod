package dev.voidedaries.aries.client;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigWatcher implements Runnable {
    private final Path configFile = AriesConfig.getConfigFile();
    private final WatchService watcherService;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private static volatile boolean ignore;

    public ConfigWatcher() throws IOException {
        this.watcherService = FileSystems.getDefault().newWatchService();

        Path dir = this.configFile.getParent();
        dir.register(
            this.watcherService,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_CREATE
        );
    }

    public static boolean isIgnoringWrites() {
        return ignore;
    }

    public static void setIgnore(boolean value) {
        ignore = value;
    }

    public static ConfigWatcher init() {
        try {
            ConfigWatcher watcher = new ConfigWatcher();

            Thread thread = new Thread(watcher, "Aries-ConfigWatcher");
            thread.setDaemon(true);
            thread.start();

            ClientLifecycleEvents.CLIENT_STOPPING.register(_ -> watcher.close());

            return watcher;
        } catch (IOException e) {
            LogUtils.getLogger().error("Failed to initialise config watcher", e);
            return null;
        }
    }

    @Override
    public void run() {
        try {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                WatchKey key = watcherService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (!(event.context() instanceof Path changed)) {
                        continue;
                    }

                    if (!configFile.getFileName().equals(changed)) {
                        continue;
                    }

                    if (isIgnoringWrites()) {
                        continue;
                    }

                    AriesConfig.reload();
                }

                if (!key.reset()) {
                    running.set(false);
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ClosedWatchServiceException ignored) {
            // Expected when the watcher is closed during shutdown.
        } catch (Exception e) {
            if (running.get()) {
                LogUtils.getLogger().error("Config watcher failed", e);
            }
        } finally {
            close();
        }
    }

    public void close() {
        if (running.getAndSet(false)) {
            try {
                watcherService.close();
            } catch (IOException e) {
                LogUtils.getLogger().error("Failed to close watcher", e);
            }
        }
    }
}

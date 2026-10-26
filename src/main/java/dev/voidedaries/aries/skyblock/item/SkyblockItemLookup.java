package dev.voidedaries.aries.skyblock.item;

import dev.voidedaries.aries.Aries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SkyblockItemLookup {
    private static volatile Map<String, SkyblockItem> SKYBLOCK_ITEM_BY_INTERNAL_NAME = Map.of();
    private static volatile CompletableFuture<Void> initialization = new CompletableFuture<>();
    private final String internalName;

    public static final SkyblockItemLookup ASPECT_OF_THE_END = new SkyblockItemLookup("ASPECT_OF_THE_END");
    public static final SkyblockItemLookup ASPECT_OF_THE_VOID = new SkyblockItemLookup("ASPECT_OF_THE_VOID");

    private SkyblockItemLookup(String internalName) {
        this.internalName = internalName;
    }

    public static void beginInit() {
        initialization = new CompletableFuture<>();
    }

    public static void init(HashMap<String, SkyblockItem> skyblockItemByInternalName) {
        SKYBLOCK_ITEM_BY_INTERNAL_NAME = Map.copyOf(skyblockItemByInternalName);
    }

    public static void finishInit() {
        // notifying latch that it has finished initializing
        initialization.complete(null);
    }

    private static void ensureInitialization() {
        CompletableFuture<Void> current = initialization;

        try {
            current.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Aries.log("Timed out waiting for SkyblockItem initialization");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Aries.log("Interrupted while waiting for SkyblockItem initialization");
        } catch (ExecutionException e) {
            Aries.log("SkyblockItem initialization failed: " + e.getCause());
        }
    }

    public SkyblockItem getSkyblockItem() {
        ensureInitialization();

        SkyblockItem skyblockItem = SKYBLOCK_ITEM_BY_INTERNAL_NAME.get(internalName);

        // item may not exist in the loaded Skyblock item data
        if (skyblockItem == null) {
            Aries.log("SkyblockItem not found for " + internalName);
            return null;
        }

        return skyblockItem;
    }

    public String getInternalName() {
        return internalName;
    }

    public Component getDisplayName() {
        SkyblockItem item = getSkyblockItem();
        return item != null ? item.displayName() : Component.empty();
    }

    public String getCleanDisplayName() {
        return getDisplayName().getString().replaceAll("§.", "");
    }

    public CompoundTag getCustomData() {
        SkyblockItem item = getSkyblockItem();
        return item != null ? item.customData() : new CompoundTag();
    }

    public List<Component> getLore() {
        SkyblockItem item = getSkyblockItem();
        return item != null ? item.lore() : List.of();
    }

}

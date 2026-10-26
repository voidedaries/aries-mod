package dev.voidedaries.aries.skyblock.repo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.skyblock.item.SkyblockItem;
import dev.voidedaries.aries.skyblock.item.SkyblockItemLookup;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NeuItemJsonParser {

    public static boolean readNeuSkyblockItemZip() {
        try (ZipFile neuZip = new ZipFile(SkyBlockRepoDownloader.NEU_REPO_ZIP.toFile())) {
            Enumeration<? extends ZipEntry> entries = neuZip.entries();
            HashMap<String, SkyblockItem> skyblockItemByInternalName = new HashMap<>();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                if (!zipEntry.getName().contains("/items/")
                    || !zipEntry.getName().endsWith(".json")
                    || zipEntry.getName().endsWith("_NPC.json")) {
                    continue;
                }

                try (
                    JsonReader reader =
                        new JsonReader(new InputStreamReader(neuZip.getInputStream(zipEntry), StandardCharsets.UTF_8))
                ) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                    SkyblockItem skyblockItem = SkyblockItem.convertJsonToSkyblockItem(json);
                    skyblockItemByInternalName.put(skyblockItem.internalName(), skyblockItem);
                } catch (Exception e) {
                    Aries.warn("Cannot read entry: {}", zipEntry.getName(), e);
                }
            }

            if (skyblockItemByInternalName.isEmpty()) {
                Aries.warn("NEU repository parsing produced no items; keeping previous data");
                return false;
            }

            SkyblockItemLookup.init(skyblockItemByInternalName);
            Aries.log("Loaded {} NEU Skyblock items", skyblockItemByInternalName.size());
            return true;

        } catch (Exception ex) {
            Aries.warn("Cannot read NEU Item Zip", ex);
            return false;
        }
    }

}

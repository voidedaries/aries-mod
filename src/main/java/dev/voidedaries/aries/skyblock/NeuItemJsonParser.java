package dev.voidedaries.aries.skyblock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.skyblock.repo.SkyBlockRepoDownloader;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NeuItemJsonParser {

    public static void readNeuSkyblockItemZip() {
        try (ZipFile neuZip = new ZipFile(SkyBlockRepoDownloader.NEU_REPO_ZIP.toFile())) {
            Enumeration<? extends ZipEntry> entries = neuZip.entries();
            HashMap<String, SkyblockItem> skyblockItemByInternalName = new HashMap<>();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                try {
                    if (zipEntry.getName().contains("/items/")
                        && zipEntry.getName().endsWith(".json")
                        && !zipEntry.getName().endsWith("_NPC.json")
                    ) {
                        JsonReader reader =
                            new JsonReader(
                                new InputStreamReader(neuZip.getInputStream(zipEntry), StandardCharsets.UTF_8)
                            );
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                        SkyblockItem skyblockItem = SkyblockItem.convertJsonToSkyblockItem(json);
                        skyblockItemByInternalName.put(skyblockItem.internalName(), skyblockItem);
                    }
                } catch (Exception e) {
                    Aries.warn("Cannot read entry: {}", zipEntry.getName(), e);
                }
            }

            SkyblockItemLookup.init(skyblockItemByInternalName);
        } catch (Exception ex) {
            Aries.warn("Cannot read NEU Item Zip", ex);
        }
    }

}

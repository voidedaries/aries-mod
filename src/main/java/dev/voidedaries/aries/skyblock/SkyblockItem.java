package dev.voidedaries.aries.skyblock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record SkyblockItem(
    Identifier itemId,
    Component displayName,
    String internalName,
    List<Component> lore,
    int damage,
    @Nullable CompoundTag nbt
) {

    public static SkyblockItem convertJsonToSkyblockItem(JsonObject json) {
        Identifier itemId = Identifier.parse(json.get("itemid").getAsString());
        Component displayName = Component.literal(json.get("displayname").getAsString());
        String internalName = json.get("internalname").getAsString();
        List<Component> lore = jsonArrayToLore(json.get("lore").getAsJsonArray());
        int damage = json.get("damage").getAsInt();
        //todo convert old nbt formatting to newer formatting
        //CompoundTag nbt = TagParser.parseCompoundFully(json.get("nbttag").getAsString());

        return new SkyblockItem(itemId, displayName, internalName, lore, damage, null);
    }

    private static List<Component> jsonArrayToLore(JsonArray array) {
        List<Component> lore = new ArrayList<>();

        for (JsonElement element : array) {
            Component line = Component.literal(element.getAsString());
            lore.add(line);
        }

        return lore;
    }

}

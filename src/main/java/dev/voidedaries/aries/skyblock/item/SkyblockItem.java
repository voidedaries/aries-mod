package dev.voidedaries.aries.skyblock.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public record SkyblockItem(
    Identifier itemId,
    Component displayName,
    String internalName,
    int damage,
    List<Component> lore,
    CompoundTag customData
) {

    public static SkyblockItem convertJsonToSkyblockItem(JsonObject json) {
        Identifier itemId = Identifier.parse(json.get("itemid").getAsString());
        Component displayName = Component.literal(json.get("displayname").getAsString());
        String internalName = json.get("internalname").getAsString();
        int damage = json.get("damage").getAsInt();

        List<Component> lore = jsonArrayToLore(json.get("lore").getAsJsonArray());
        CompoundTag customData = convertLegacyNbt(json.get("nbttag").getAsString());

        return new SkyblockItem(itemId, displayName, internalName, damage, lore, customData);
    }

    private static List<Component> jsonArrayToLore(JsonArray array) {
        List<Component> lore = new ArrayList<>();

        for (JsonElement element : array) {
            Component line = Component.literal(element.getAsString());
            lore.add(line);
        }

        return lore;
    }

    private static CompoundTag convertLegacyNbt(String legacyNbt) {
        String converted = convertLegacyLists(legacyNbt);

        try {
            return TagParser.parseCompoundFully(converted);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse legacy NEU NBT", e);
        }
    }

    private static String convertLegacyLists(String legacyNbt) {
        StringBuilder result = new StringBuilder(legacyNbt.length());

        Deque<Character> containers = new ArrayDeque<>();

        boolean inString = false;
        boolean escaped = false;
        boolean atListElementStart = false;

        for (int index = 0; index < legacyNbt.length(); index++) {
            char current = legacyNbt.charAt(index);

            if (inString) {
                result.append(current);

                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    inString = false;
                }

                continue;
            }

            if (current == '"') {
                inString = true;
                result.append(current);
                continue;
            }

            if (current == '[') {
                containers.push('[');
                atListElementStart = true;
                result.append(current);
                continue;
            }

            if (current == '{') {
                containers.push('{');
                atListElementStart = false;
                result.append(current);
                continue;
            }

            if (current == ']' || current == '}') {
                if (!containers.isEmpty()) {
                    containers.pop();
                }

                atListElementStart = false;
                result.append(current);
                continue;
            }

            if (current == ',' && !containers.isEmpty() && containers.peek() == '[') {
                atListElementStart = true;
                result.append(current);
                continue;
            }

            if (atListElementStart) {
                if (Character.isWhitespace(current)) {
                    result.append(current);
                    continue;
                }

                int indexEnd = index;

                while (indexEnd < legacyNbt.length() && Character.isDigit(legacyNbt.charAt(indexEnd))) {
                    indexEnd++;
                }

                if (indexEnd > index) {
                    int colonIndex = indexEnd;

                    while (colonIndex < legacyNbt.length() && Character.isWhitespace(legacyNbt.charAt(colonIndex))) {
                        colonIndex++;
                    }

                    if (colonIndex < legacyNbt.length() && legacyNbt.charAt(colonIndex) == ':') {
                        index = colonIndex;
                        atListElementStart = false;
                        continue;
                    }
                }

                atListElementStart = false;
            }

            result.append(current);
        }

        return result.toString();
    }

}

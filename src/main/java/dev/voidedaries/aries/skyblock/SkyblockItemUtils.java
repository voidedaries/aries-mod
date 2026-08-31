package dev.voidedaries.aries.skyblock;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class SkyblockItemUtils {

    private SkyblockItemUtils() {}

    public static Optional<Component> findLoreLine(List<Component> lore, String text) {
        for (Component line : lore) {
            String cleanLine = cleanLoreLine(line);

            if (cleanLine.startsWith(text)) {
                return Optional.of(line);
            }
        }

        return Optional.empty();
    }

    public static Optional<Component> findLoreLineAfter(List<Component> lore, String after, String text) {
        boolean found = false;

        for (Component line : lore) {
            String cleanLine = cleanLoreLine(line);

            if (!found) {
                if (cleanLine.startsWith(after)) {
                    found = true;
                }

                continue;
            }

            if (cleanLine.startsWith(text)) {
                return Optional.of(line);
            }
        }

        return Optional.empty();
    }

    public static Optional<String> findLoreTextAfter(
        List<Component> lore,
        String after,
        String text
    ) {
        boolean found = false;

        for (Component line : lore) {
            String cleanLine = cleanLoreLine(line);

            if (!found) {
                if (cleanLine.startsWith(after)) {
                    found = true;
                }

                continue;
            }

            if (cleanLine.startsWith(text)) {
                return Optional.of(cleanLine);
            }
        }

        return Optional.empty();
    }

    private static String cleanLoreLine(Component line) {
        return line.getString().replaceAll("§.", "").trim();
    }

}

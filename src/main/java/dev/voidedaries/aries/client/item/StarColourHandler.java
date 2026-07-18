package dev.voidedaries.aries.client.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StarColourHandler {

    public static Component modify(Component name) {
        int masterStars = getMasterStars(name);

        if (masterStars <= 0) {
            return name;
        }

        MutableComponent result = Component.empty().setStyle(name.getStyle());

        for (Component child : name.getSiblings()) {
            String text = child.getString();

            if (text.contains("✪")) {

                for (int i = 0; i < text.length(); i++) {
                    result.append(
                        Component.literal("✪").setStyle(
                            child.getStyle().withColor(i < masterStars ? ChatFormatting.RED : ChatFormatting.GOLD)
                        )
                    );
                }

            }
            else if (!text.contains("➊") &&
                !text.contains("➋") &&
                !text.contains("➌") &&
                !text.contains("➍") &&
                !text.contains("➎")) {

                result.append(child);
            }
        }

        return result;
    }

    private static int getMasterStars(Component name) {
        String text = name.getString();

        if (text.contains("➊")) return 1;
        if (text.contains("➋")) return 2;
        if (text.contains("➌")) return 3;
        if (text.contains("➍")) return 4;
        if (text.contains("➎")) return 5;

        return 0;
    }

}

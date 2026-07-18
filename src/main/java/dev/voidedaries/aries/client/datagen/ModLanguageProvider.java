package dev.voidedaries.aries.client.datagen;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.item.ItemRarity;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {

    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void addTranslation(
        FabricLanguageProvider.TranslationBuilder translations,
        String category,
        String id,
        String name,
        String description
    ) {
        String key = "gui.category." + category + "." + id;

        translations.add(
            key + ".name",
            name
        );

        translations.add(
            key + ".description",
            description
        );
    }

    @Override
    public void generateTranslations(@NotNull HolderLookup.Provider registries, TranslationBuilder translations) {
        try {
            translations.add(FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID).orElseThrow()
                    .findPath("en_us.json").orElseThrow());
        } catch (IOException e) {
            Aries.log("Unable to add translations from existing en_us.json", e);
        }

        addTranslation(
            translations, AriesCategory.CHAT.getId(),
            "compact_chat",
            "Compact Chat",
            "combines identical messages sent recently."
        );

        addTranslation(
            translations, AriesCategory.CHAT.getId(),
            "compact_chat_time",
            "Compact Chat Duration",
            "time in seconds during which duplicate messages are merged."
        );

        addTranslation(
            translations, AriesCategory.CHAT.getId(),
            "peek_chat",
            "Peek Chat",
            "allows viewing chat without opening the screen."
        );

        addTranslation(
            translations, AriesCategory.CHAT.getId(),
            "scroll_peek_chat",
            "Scroll Peek Chat",
            "allows scrolling through chat while peeking chat."
        );

        addTranslation(
            translations, AriesCategory.COMMANDS.getId(),
            "skip_command_confirmation",
            "Skip Command Confirmation",
            "skips command confirmation prompts caused by missing client-side command permissions."
        );

        addTranslation(
            translations, AriesCategory.VISUALS.getId(),
            "etherwarp_outline",
            "Etherwarp Outline",
            "shows an outline of the Etherwarp destination."
        );

        addTranslation(
            translations, AriesCategory.VISUALS.getId(),
            "etherwarp_outline.width",
            "Etherwarp Width",
            "change the outline width for etherwarping."
        );

        addTranslation(
            translations, AriesCategory.VISUALS.getId(),
            "etherwarp_outline.valid_color",
            "Valid Etherwarp Color",
            "change the color used for a valid etherwarp."
        );

        addTranslation(
            translations, AriesCategory.VISUALS.getId(),
            "etherwarp_outline.invalid_color",
            "Invalid Etherwarp Color",
            "change the color used for a invalid etherwarp."
        );

        addTranslation(
            translations, AriesCategory.VISUALS.getId(),
            "item_rarity_scaling",
            "Item Rarity Scaling",
            "changes the scale of dropped items."
        );

        addItemRarityScalingTranslations(translations);

        addTranslation(
            translations, AriesCategory.VISUALS.getId(),
            "old_master_star_colours",
            "Old Master Star Formatting",
            "changes master starred item names to the old star formatting"
        );
    }

    private static void addItemRarityScalingTranslations(
        TranslationBuilder translations
    ) {
        for (ItemRarity rarity : ItemRarity.values()) {
            String name = formatRarityName(rarity);

            addTranslation(
                translations,
                AriesCategory.VISUALS.getId(),
                "item_rarity_" + rarity.name().toLowerCase(Locale.ROOT),
                name + " Scaling",
                "Changes the scale of dropped " + name + " items."
            );
        }
    }

    private static String formatRarityName(ItemRarity rarity) {
        String name = rarity.name()
            .toLowerCase(Locale.ROOT)
            .replace('_', ' ');

        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }


}
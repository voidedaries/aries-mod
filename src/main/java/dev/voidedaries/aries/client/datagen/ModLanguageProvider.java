package dev.voidedaries.aries.client.datagen;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.skyblock.item.ItemRarity;
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
        AriesCategory category,
        String id,
        String name,
        String description
    ) {
        String key = "gui.category." + category.getId() + "." + id;

        translations.add(
            key + ".name",
            name
        );

        translations.add(
            key + ".description",
            description
        );
    }

    public static void addButtonTranslation(
        FabricLanguageProvider.TranslationBuilder translations,
        AriesCategory category,
        String id,
        String name
    ) {
        String key = "gui.category." + category.getId() + "." + id + ".button";

        translations.add(key, name);
    }

    @Override
    public void generateTranslations(@NotNull HolderLookup.Provider registries, TranslationBuilder translations) {
        try {
            translations.add(FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID).orElseThrow()
                    .findPath("en_us.json").orElseThrow());
        } catch (IOException e) {
            Aries.log("Unable to add translations from existing en_us.json", e);
        }

        addButtonTranslation(
            translations, AriesCategory.SETTINGS,
            "gui_location",
            "GUI Editor"
        );

        addTranslation(
            translations, AriesCategory.DEV,
            "hypixel_environment_override",
            "Hypixel Environment Override",
            "overrides Hypixel and SkyBlock detection for testing"
        );

        addTranslation(
            translations, AriesCategory.DEV,
            "minecraft_warning_log_suppression",
            "Suppress Minecraft Warnings",
            "suppress selected Minecraft warnings from appearing in the game log."
        );

        addTranslation(
            translations, AriesCategory.DEV,
            "yggdrasil_log_suppression",
            "Suppress Yggdrasil Errors",
            "hides repeated texture signature errors from the log."
        );

        addTranslation(
            translations, AriesCategory.CHAT,
            "compact_chat",
            "Compact Chat",
            "combines identical messages sent recently."
        );

        addTranslation(
            translations, AriesCategory.CHAT,
            "compact_chat_time",
            "Compact Chat Duration",
            "time in seconds during which duplicate messages are merged."
        );

        addTranslation(
            translations, AriesCategory.CHAT,
            "peek_chat",
            "Peek Chat",
            "allows viewing chat without opening the screen."
        );

        addTranslation(
            translations, AriesCategory.CHAT,
            "scroll_peek_chat",
            "Scroll Peek Chat",
            "allows scrolling through chat while peeking chat."
        );

        addTranslation(
            translations, AriesCategory.COMMANDS,
            "skip_command_confirmation",
            "Skip Command Confirmation",
            "skips command confirmation prompts caused by missing client-side command permissions."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "ashfang_mob_color",
            "Ashfang Mob Color",
            "change The Color of Ashfangs Followers."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "ashfang_mob_color.follower",
            "Ashfang Follower Color",
            "change the color of the Ashfang Follower."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "ashfang_mob_color.acolyte",
            "Ashfang Acolyte Color",
            "change the color of the Ashfang Acolyte."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "ashfang_mob_color.underling",
            "Ashfang Underling Color",
            "change the color of the Ashfang Underling."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "ashfang_death_mode",
            "Ashfang Death Mode",
            "you may want to avoid finding out what happens."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "blazing_soul_trajectory",
            "Blazing Soul Trajectory",
            "displays the trajectory of the Blazing Soul based on player direction."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "blazing_soul_trajectory.color",
            "Blazing Soul Trajectory Color",
            "sets the color of the Blazing Soul trajectory."
        );

        addTranslation(
            translations, AriesCategory.CRIMSON_ISLE,
            "blazing_soul_trajectory.width",
            "Blazing Soul Trajectory Width",
            "sets the width of the Blazing Soul trajectory."
        );

        addTranslation(
            translations, AriesCategory.SETTINGS,
            "pause_menu_button",
            "Pause Menu Button",
            "adds an Aries button to the pause menu."
        );

        addTranslation(
            translations, AriesCategory.SETTINGS,
            "gui_location_button",
            "GUI Editor",
            "open the GUI Editor to change overlay positions and attributes."
        );

        addTranslation(
            translations, AriesCategory.SETTINGS,
            "gui_location_keybind",
            "GUI Editor Keybind",
            "provides quick access to the GUI Editor screen."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "etherwarp_outline",
            "Etherwarp Outline",
            "shows an outline of the Etherwarp destination."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "etherwarp_outline.mode",
            "Etherwarp Mode",
            "choose how the target block is highlighted."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "etherwarp_outline.width",
            "Etherwarp Width",
            "change the outline width for etherwarping."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "etherwarp_outline.valid_color",
            "Valid Etherwarp Color",
            "change the color used for a valid etherwarp."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "etherwarp_outline.invalid_color",
            "Invalid Etherwarp Color",
            "change the color used for an invalid etherwarp."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "item_rarity_scaling",
            "Item Rarity Scaling",
            "changes the scale of dropped items."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "low_fire",
            "Low Fire",
            "controls how low the fire overlay is displayed while burning."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "second_layer_leather_armor",
            "Hide Leather Armour Overlay",
            "disable the secondary layer on leather armour."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "third_person_nametag",
            "Third Person Name Tag",
            "shows your own name tag in third person."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "hide_armor",
            "Armor Customisation",
            "customize your appearance by toggling equipped armor pieces on or off from your inventory."
        );

        addTranslation(
            translations, AriesCategory.VISUALS,
            "old_master_star_colours",
            "Old Master Star Formatting",
            "changes master starred item names to the old star formatting"
        );

        addTranslation(
            translations, AriesCategory.MISC,
            "fps_display",
            "FPS Display",
            "displays your current frames per second as a customisable HUD element."
        );

        addHeldItemCustomisationTranslations(translations);

        addItemRarityScalingTranslations(translations);

        addPlayerCustomisationTranslations(translations);
    }

    private static void addPlayerCustomisationTranslations(
        TranslationBuilder translations
    ) {
        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "player_customisation",
            "Player Customisation",
            "Customises the scaling and the height, width and depth of the player."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "player_customisation.scale",
            "Player Scaling",
            "Changes the size of the player."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "player_customisation.height",
            "Player Height",
            "Changes the height of the player."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "player_customisation.width",
            "Player Width",
            "Changes the width of the player."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "player_customisation.depth",
            "Player Depth",
            "Changes the depth of the player."
        );
    }

    private static void addHeldItemCustomisationTranslations(
        TranslationBuilder translations
    ) {
        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "held_item_customisation",
            "Held Item Customisation",
            "Customises the size, position and animations of held items."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "held_item_customisation.scale",
            "Item Scale",
            "Changes the size of held items."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "held_item_customisation.position_x",
            "X Position",
            "Changes the horizontal position of held items."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "held_item_customisation.position_y",
            "Y Position",
            "Changes the vertical position of held items."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "held_item_customisation.position_z",
            "Z Position",
            "Changes the depth position of held items."
        );

        addTranslation(
            translations,
            AriesCategory.VISUALS,
            "held_item_customisation.swing_speed",
            "Swing Speed",
            "Changes the swing speed of held items."
        );
    }

    private static void addItemRarityScalingTranslations(
        TranslationBuilder translations
    ) {
        for (ItemRarity rarity : ItemRarity.values()) {
            String name = formatRarityName(rarity);

            addTranslation(
                translations,
                AriesCategory.VISUALS,
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
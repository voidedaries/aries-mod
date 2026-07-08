package dev.voidedaries.aries.client.datagen;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {

    public ModLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generateTranslations(@NotNull HolderLookup.Provider registries, TranslationBuilder translations) {
        try {
            translations.add(FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID).orElseThrow()
                    .findPath("en_us.json").orElseThrow());
        } catch (IOException e) {
            Aries.log("Unable to add translations from existing en_us.json", e);
        }
    }


}
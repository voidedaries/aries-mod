package dev.voidedaries.aries;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class ModConstants {
    public static final String MOD_ID = "aries";

    public static final Identifier ARIES_LOGO = Aries.id("logo.png");

    public static final String GITHUB_URL = "https://github.com/voidedaries/aries-mod";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/aries";
    public static final String DISCORD_URL = "https://discord.gg/E5DYwuKPK3";

    /**
     * Version format:
     * MAJOR.MINOR.PATCH[-STAGE]
     * example: 0.1.0-alpha.1 would be the screen
     * Rules:
     * - MAJOR = breaking changes / rewrites
     * - MINOR = new features
     * - PATCH = bug fixes / tweaks
     * - STAGE = optional (alpha, beta, release)
     */
    public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow()
            .getMetadata()
            .getVersion()
            .getFriendlyString();

    public static final String version = FabricLoader.getInstance()
        .getModContainer(ModConstants.MOD_ID)
        .map(
            mod -> mod.getMetadata().getVersion().getFriendlyString()
        ).orElse("unknown");

    public static final String minecraftVersion = FabricLoader.getInstance().getModContainer("minecraft")
        .map(container -> container.getMetadata().getVersion().getFriendlyString())
        .orElse("Unknown");

    public static final String displayVersion = VERSION;
}

package dev.voidedaries.aries;

import net.fabricmc.loader.api.FabricLoader;

public class ModConstants {
    public static final String MOD_ID = "aries";

    private static final String version = FabricLoader.getInstance()
        .getModContainer(ModConstants.MOD_ID)
        .map(
            mod -> mod.getMetadata().getVersion().getFriendlyString()
        ).orElse("unknown");

    public static final String displayVersion = version.contains("-")
        ? version.substring(0, version.indexOf("-"))
        : version;

    public static final String GITHUB_URL = "https://github.com/voidedaries/aries-mod";

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
}

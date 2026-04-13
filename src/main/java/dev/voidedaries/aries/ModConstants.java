package dev.voidedaries.aries;

import net.fabricmc.loader.api.FabricLoader;

public class ModConstants {
    public static final String MOD_ID = "aries";
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
    public static final String VERSION = FabricLoader.getInstance().getModContainer("aries").orElseThrow()
            .getMetadata()
            .getVersion()
            .getFriendlyString();

}

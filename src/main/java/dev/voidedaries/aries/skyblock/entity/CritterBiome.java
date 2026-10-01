package dev.voidedaries.aries.skyblock.entity;

import net.minecraft.resources.Identifier;

public enum CritterBiome {
    CAVERN("hypixel:cavern"),
    FOREST("hypixel:forest"),
    HAUNTED("hypixel:haunted"),
    ICY("hypixel:icy", "hypixel:icy_caves");

    private final Identifier[] biomeIds;

    CritterBiome(String... biomeIds) {
        this.biomeIds = new Identifier[biomeIds.length];

        for (int i = 0; i < biomeIds.length; i++) {
            this.biomeIds[i] = Identifier.parse(biomeIds[i]);
        }
    }

    public static CritterBiome fromIdentifier(Identifier identifier) {
        for (CritterBiome biome : values()) {
            if (biome.matches(identifier)) {
                return biome;
            }
        }

        return null;
    }

    public boolean matches(Identifier biomeId) {
        for (Identifier id : biomeIds) {
            if (id.equals(biomeId)) {
                return true;
            }
        }

        return false;
    }
}

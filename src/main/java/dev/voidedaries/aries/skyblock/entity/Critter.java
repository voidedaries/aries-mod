package dev.voidedaries.aries.skyblock.entity;

import dev.voidedaries.aries.Aries;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public enum Critter {

    CAVERNFISH("Cavernfish", CritterBiome.CAVERN),
    FLITTER("Flitter", CritterBiome.CAVERN),
    SHYWORM("Shyworm", CritterBiome.CAVERN),
    DRIFTLING("Driftling", CritterBiome.CAVERN),
    CHUCKWALLA("Chuckwalla", CritterBiome.CAVERN),
    ROCKMITE("Rockmite", CritterBiome.CAVERN),
    SCRAPPY("Scrappy", CritterBiome.CAVERN),
    SNOOZLE("Snoozle", CritterBiome.CAVERN),
    GEMZIE("Gemzie", CritterBiome.CAVERN),

    FOXTROT("Foxtrot", CritterBiome.FOREST),
    BLUEBIRD("Bluebird", CritterBiome.FOREST),
    HONEYBUG("Honeybug", CritterBiome.FOREST),
    TREEFROG("Treefrog", CritterBiome.FOREST),
    WOODCHUCKER("Woodchucker", CritterBiome.FOREST),
    FLUFFLING("Fluffling", CritterBiome.FOREST),
    HIDEONFLOOR("Hideonfloor", CritterBiome.FOREST),
    PARAKEET("Parakeet", CritterBiome.FOREST),
    MACAW("Macaw", CritterBiome.FOREST),

    AREITA("Areita", CritterBiome.HAUNTED),
    BLOODBAT("Bloodbat", CritterBiome.HAUNTED),
    DUPLICO("Duplico", CritterBiome.HAUNTED),
    GAZER("Gazer", CritterBiome.HAUNTED),
    LITTERBUG("Litterbug", CritterBiome.HAUNTED),
    SOLSNATCHER("Solsnatcher", CritterBiome.HAUNTED),
    GIMMIEGOLD("Gimmiegold", CritterBiome.HAUNTED),
    HIDEONWALL("Hideonwall", CritterBiome.HAUNTED),
    HIDEYHO("Hideyho", CritterBiome.HAUNTED),
    DOOMSPIRAL("Doomspiral", CritterBiome.HAUNTED),

    STRONGARM("Strongarm", CritterBiome.ICY),
    TEPID("Tepid", CritterBiome.ICY),
    POLARIS("Polaris", CritterBiome.ICY),
    SHUDDERSQUID("Shuddersquid", CritterBiome.ICY),
    BILLYGOAT("Billygoat", CritterBiome.ICY),
    MANTIS_SHRIMP("Mantis Shrimp", CritterBiome.ICY),
    NOZZLENOSE("Nozzlenose", CritterBiome.ICY),
    TROODON("Troodon", CritterBiome.ICY),
    WUMPA("Wumpa", CritterBiome.ICY);

    private final String name;
    private final CritterBiome biome;

    Critter(String name, CritterBiome biome) {
        this.name = name;
        this.biome = biome;
    }

    public String getName() {
        return name;
    }

    public CritterBiome getBiome() {
        return biome;
    }

    public Identifier getTexture() {
        return Aries.id(
            "textures/critter/" + name.toLowerCase(Locale.ROOT).replace(" ", "_") + ".png"
        );
    }

    public static Critter fromName(String name) {
        for (Critter critter : values()) {
            if (critter.name.equals(name)) {
                return critter;
            }
        }

        return null;
    }

}

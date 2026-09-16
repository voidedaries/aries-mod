package dev.voidedaries.aries.skyblock.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyblockEntity {

    private final ArmorStand nameTagEntity;
    private final Set<LivingEntity> modelEntities = new HashSet<>();

    private String name = "";
    private String health = "0";
    private String maxHealth = "0";

    private boolean dying;

    public SkyblockEntity(ArmorStand nameTagEntity, LivingEntity modelEntity) {
        this.nameTagEntity = nameTagEntity;
        this.modelEntities.add(modelEntity);

        update();
    }

    public ArmorStand getNameTagEntity() {
        return nameTagEntity;
    }

    public Set<LivingEntity> getModelEntities() {
        return modelEntities;
    }

    public LivingEntity getModelEntity() {
        return modelEntities.stream().findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }

    public String getHealth() {
        return health;
    }

    public String getMaxHealth() {
        return maxHealth;
    }

    public boolean isDying() {
        return dying;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public void update() {
        NameTagData data = parseNameTag(nameTagEntity);

        if (data == null) {
            return;
        }

        this.name = data.name();
        this.health = data.health();
        this.maxHealth = data.maxHealth();
    }

    private static NameTagData parseNameTag(ArmorStand entity) {
        if (!entity.hasCustomName()) {
            return null;
        }

        String name = entity.getName().getString();

        String regex =
            "(?:﴾ )?\\[Lv\\d+]\\s+\\S+\\s+(.+)\\s+(\\d+[.,]?\\d*[kMB]?)/(\\d+[.,]?\\d*[kMB]?)❤(?: ﴿)?(?: ✯)?";

        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(name);

        if (!matcher.matches()) {
            return null;
        }

        return new NameTagData(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    private record NameTagData(String name, String health, String maxHealth) {}
}

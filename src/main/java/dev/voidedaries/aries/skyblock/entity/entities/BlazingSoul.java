package dev.voidedaries.aries.skyblock.entity.entities;

import dev.voidedaries.aries.Aries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class BlazingSoul {

    private static final String LAUNCH_NAME = "CLICK TO LAUNCH";

    private final ArmorStand entity;

    public BlazingSoul(ArmorStand entity) {
        this.entity = entity;
    }

    public ArmorStand getEntity() {
        return entity;
    }

    public static boolean is(ArmorStand armorStand) {
        if (!armorStand.hasCustomName()) {
            return false;
        }

        return LAUNCH_NAME.equals(Objects.requireNonNull(armorStand.getCustomName()).getString());
    }

    public Vec3 getPosition(float partialTickTime) {
        return entity.getPosition(partialTickTime);
    }

    public Vec3 getLaunchDirection() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return Vec3.ZERO;
        }

        return minecraft.player.getLookAngle();
    }
}
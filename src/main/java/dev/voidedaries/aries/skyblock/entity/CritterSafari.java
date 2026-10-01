package dev.voidedaries.aries.skyblock.entity;

import java.util.HashSet;
import java.util.Set;

public class CritterSafari {
    private final Set<Critter> caughtCritters = new HashSet<>();
    private boolean active;

    public void start() {
        caughtCritters.clear();
        active = true;
    }

    public void end() {
        active = false;
    }

    public void catchCritter(Critter critter) {
        if (!active) {
            return;
        }

        caughtCritters.add(critter);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCaught(Critter critter) {
        return caughtCritters.contains(critter);
    }

}

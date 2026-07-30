package dev.voidedaries.aries.client.render.player;

import java.util.Map;
import java.util.UUID;

public final class PlayerAppearanceRegistry {

    private PlayerAppearanceRegistry() {}

    // client-side appearance overrides for trusted players.
    // these are intentionally not synced and only affect clients running Aries.
    public static final Map<UUID, PlayerAppearance> APPEARANCES = Map.of(
        UUID.fromString("9ec585c6-9393-49bb-88e8-41129022dad8"),
        new PlayerAppearance("VoidedAries", 1.2f, 1f, 1f, 1f)
    );

    public static PlayerAppearance get(UUID uuid) {
        return APPEARANCES.get(uuid);
    }

}

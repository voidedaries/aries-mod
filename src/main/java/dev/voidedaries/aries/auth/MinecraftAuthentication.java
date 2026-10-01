package dev.voidedaries.aries.auth;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import java.util.UUID;

/**
 * Uses Minecraft's session authentication system to authenticate the player.
 *
 * <p>This authentication approach is based on the method used by
 * Ursa Minor, which uses Minecraft's session server to verify player identity.</p>
 *
 * @see <a href="https://github.com/NotEnoughUpdates/ursa-minor">Ursa Minor</a>
 */
public final class MinecraftAuthentication {

    private MinecraftAuthentication() {}

    public static String authenticate() throws AuthenticationException {
        Minecraft minecraft = Minecraft.getInstance();
        User user = minecraft.getUser();

        MinecraftSessionService sessionService = minecraft.services().sessionService();

        String serverId = UUID.randomUUID().toString();

        sessionService.joinServer(user.getProfileId(), user.getAccessToken(), serverId);

        return serverId;
    }

}

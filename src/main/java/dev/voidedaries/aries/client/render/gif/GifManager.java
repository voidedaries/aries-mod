package dev.voidedaries.aries.client.render.gif;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.skyblock.entity.Critter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GifManager {
    private static final Map<Critter, GifAnimation> critterGifAnimations = new HashMap<>();
    private static final Map<Critter, GifTexture> critterGifTextures = new HashMap<>();
    private static final Map<Critter, Integer> critterGifFrames = new HashMap<>();

    private static boolean gifsInitialized;

    private GifManager() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(_ -> {
            if (!gifsInitialized) {
                for (Critter critter : Critter.values()) {
                    loadCritterGifs(critter);
                }

                gifsInitialized = true;
            }

            updateAnimations();
        });
    }

    private static void updateAnimations() {
        long currentTime = System.currentTimeMillis();

        for (Critter critter : critterGifAnimations.keySet()) {
            GifAnimation animation = critterGifAnimations.get(critter);
            GifTexture texture = critterGifTextures.get(critter);

            int frame = animation.getFrameIndexAtTime(currentTime);
            int previousFrame = critterGifFrames.get(critter);

            if (frame == previousFrame) {
                continue;
            }

            critterGifFrames.put(critter, frame);
            texture.update(animation.getFrame(frame).image());
        }
    }

    public static Identifier getCritterTexture(Critter critter) {
        GifTexture texture = critterGifTextures.get(critter);

        if (texture != null) {
            return texture.getIdentifier();
        }

        return critter.getTexture();
    }

    private static void loadCritterGifs(Critter critter) {
        Minecraft minecraft = Minecraft.getInstance();

        String filename = critter.getName().toLowerCase(Locale.ROOT).replace(" ", "_");
        Identifier resourceId = Aries.id("textures/critter/" + filename + ".gif");

        Resource resource = minecraft.getResourceManager().getResource(resourceId).orElse(null);

        if (resource == null) {
            return;
        }

        try (InputStream inputStream = resource.open()) {
            GifAnimation animation = GifInspector.inspect(inputStream);

            Identifier textureId = Aries.id("gif/" + filename);
            GifTexture texture = new GifTexture(textureId, animation.getFrame(0).image());

            texture.register();

            critterGifAnimations.put(critter, animation);
            critterGifTextures.put(critter, texture);
            critterGifFrames.put(critter, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

package dev.voidedaries.aries.client.render;

import dev.voidedaries.aries.client.render.entity.BlazingSoulRenderer;
import dev.voidedaries.aries.skyblock.entity.entities.BlazingSoul;
//? if 26.2
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public class EntityRendererManager {

    private static final List<BlazingSoul> BLAZING_SOULS = new ArrayList<>();

    private EntityRendererManager() {}

    public static void init() {
        //? if 26.2
        Event<LevelExtractionEvents.EndExtraction> endExtraction = LevelExtractionEvents.END_EXTRACTION;
        //? if 26.1.2
        //Event<LevelRenderEvents.EndExtraction> endExtraction = LevelRenderEvents.END_EXTRACTION;

        endExtraction.register(context -> {
            ClientLevel level = context.level();

            BLAZING_SOULS.clear();

            for (Entity entity : level.entitiesForRendering()) {
                if (!(entity instanceof ArmorStand armorStand)) {
                    continue;
                }

                if (!BlazingSoul.is(armorStand)) {
                    continue;
                }

                BLAZING_SOULS.add(new BlazingSoul(armorStand));
            }

            BlazingSoulRenderer.update(level, BLAZING_SOULS);
        });

        LevelRenderEvents.AFTER_SOLID_FEATURES.register(context -> {
            for (BlazingSoul blazingSoul : BLAZING_SOULS) {
                BlazingSoulRenderer.render(blazingSoul, context);
            }
        });
    }

}

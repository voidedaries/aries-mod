package dev.voidedaries.aries.client.gui.location;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class AriesHudManager {

    public static void init() {
        HudElementRegistry.addLast(
            Aries.id("hud"), ((graphics, _) -> render(graphics))
        );
    }

    public static void render(GuiGraphicsExtractor graphics) {
        for (HudRenderable hud : getHudElements()) {
            if (!hud.isHudEnabled()) {
                continue;
            }

            HudPosition position = hud.getHudPosition();

            hud.renderHud(graphics, position.getX(), position.getY());
        }
    }

    public static List<HudRenderable> getHudElements() {
        List<HudRenderable> elements = new ArrayList<>();

        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            if (feature instanceof HudRenderable hud) {
                elements.add(hud);
            }
        }

        return elements;
    }

}

package dev.voidedaries.aries.client.gui.location;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AriesHudManager {
    private static int lastScreenWidth = -1;
    private static int lastScreenHeight = -1;

    private static final Map<HudRenderable, HudBounds> lastHudBounds = new HashMap<>();

    public static void init() {
        HudElementRegistry.addLast(
            Aries.id("hud"), ((graphics, _) -> render(graphics))
        );
    }

    public static void render(GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        handleResolutionChange(screenWidth, screenHeight);

        //? if 26.2
        Screen screen = minecraft.gui.screen();
        //? if 26.1.2
        //Screen screen = minecraft.screen;

        if (screen instanceof GUILocationsScreen) {
            return;
        }

        lastHudBounds.clear();

        for (HudRenderable hud : getHudElements()) {
            if (!hud.isHudEnabled()) {
                continue;
            }

            HudBounds bounds = renderHud(graphics, hud);
            lastHudBounds.put(hud, bounds);
        }
    }

    private static void handleResolutionChange(int screenWidth, int screenHeight) {
        if (lastScreenWidth == -1 || lastScreenHeight == -1) {
            lastScreenWidth = screenWidth;
            lastScreenHeight = screenHeight;
            return;
        }

        if (screenWidth == lastScreenWidth && screenHeight == lastScreenHeight) {
            return;
        }

        relocateHudElements(lastScreenWidth, lastScreenHeight, screenWidth, screenHeight);

        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;

        AriesConfig.save();
    }

    private static void relocateHudElements(
        int oldScreenWidth, int oldScreenHeight,
        int newScreenWidth, int newScreenHeight
    ) {
        for (HudRenderable hud : getHudElements()) {
            HudBounds oldBounds = lastHudBounds.get(hud);

            if (oldBounds == null) {
                continue;
            }

            HudPosition position = hud.getHudPosition();
            HudBounds hudBounds = hud.getHudBounds();

            HudBounds newBounds = new HudBounds(
                0, 0,
                Math.round(hudBounds.width() * position.getScale()),
                Math.round(hudBounds.height() * position.getScale())
            );

            GuiRegions region = getHudRegion(oldBounds.x(), oldBounds.y(), oldScreenWidth, oldScreenHeight);

            if (region == null) {
                continue;
            }

            GuiBounds oldRegion = region.getRegion().toScreenBounds(oldScreenWidth, oldScreenHeight);
            GuiBounds newRegion = region.getRegion().toScreenBounds(newScreenWidth, newScreenHeight);

            double oldRegionWidth = oldRegion.getBottomRightX() - oldRegion.getTopLeftX();
            double oldRegionHeight = oldRegion.getBottomRightY() - oldRegion.getTopLeftY();

            double newRegionWidth = newRegion.getBottomRightX() - newRegion.getTopLeftX();
            double newRegionHeight = newRegion.getBottomRightY() - newRegion.getTopLeftY();

            double oldAvailableWidth = oldRegionWidth - oldBounds.width();
            double oldAvailableHeight = oldRegionHeight - oldBounds.height();

            if (oldAvailableWidth <= 0 || oldAvailableHeight <= 0) {
                continue;
            }

            double relativeX = (oldBounds.x() - oldRegion.getTopLeftX()) / oldAvailableWidth;
            double relativeY = (oldBounds.y() - oldRegion.getTopLeftY()) / oldAvailableHeight;

            double newAvailableWidth = Math.max(0, newRegionWidth - newBounds.width());
            double newAvailableHeight = Math.max(0, newRegionHeight - newBounds.height());

            int newX = (int) Math.round(newRegion.getTopLeftX() + relativeX * newAvailableWidth);
            int newY = (int) Math.round(newRegion.getTopLeftY() + relativeY * newAvailableHeight);

            position.setPosition(
                Math.clamp(newX, 0, newScreenWidth - newBounds.width()),
                Math.clamp(newY, 0, newScreenHeight - newBounds.height())
            );
        }
    }

    private static GuiRegions getHudRegion(int x, int y, int screenWidth, int screenHeight) {
        for (GuiRegions region : GuiRegions.values()) {
            GuiBounds bounds = region.getRegion().toScreenBounds(screenWidth, screenHeight);

            if (bounds.contains(x, y)) {
                return region;
            }
        }

        return null;
    }

    public static HudBounds renderHud(GuiGraphicsExtractor graphics, HudRenderable hud) {
        HudPosition position = hud.getHudPosition();

        graphics.pose().pushMatrix();
        graphics.pose().translate(position.getX(),  position.getY());
        graphics.pose().scale(position.getScale(), position.getScale());

        HudBounds bounds = hud.renderHud(graphics, 0, 0);

        graphics.pose().popMatrix();

        return new HudBounds(
            position.getX(), position.getY(),
            Math.round(bounds.width() * position.getScale()),
            Math.round(bounds.height() * position.getScale())
        );
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

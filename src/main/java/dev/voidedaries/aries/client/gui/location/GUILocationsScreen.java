package dev.voidedaries.aries.client.gui.location;

import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.gui.AriesScreen;
import dev.voidedaries.aries.client.gui.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GUILocationsScreen extends Screen {

    private HudRenderable selectedHud;
    private HudRenderable draggableHud;
    private int dragOffsetX;
    private int dragOffsetY;

    private final Map<HudRenderable, HudBounds> hudBounds = new HashMap<>();

    private final Screen parent;

    public GUILocationsScreen(Screen parent) {
        super(Component.translatable("gui.locations_screen"));
        this.parent = parent;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.fill(0, 0, width, height, 0x11000000);

        renderRegions(graphics);
        renderHudElements(graphics);
    }

    private void renderRegions(GuiGraphicsExtractor graphics){
        int thirdWidth = width / 3;
        int thirdHeight = height / 3;

        int lineColor = 0x88FFFFFF;

        graphics.fill(thirdWidth, 0, thirdWidth + 1, height, lineColor);

        graphics.fill(thirdWidth * 2, 0, thirdWidth * 2 + 1, height, lineColor);

        graphics.fill(0, thirdHeight, width, thirdHeight + 1, lineColor);

        graphics.fill(0, thirdHeight * 2, width, thirdHeight * 2 + 1, lineColor);
    }

    private void renderHudElements(GuiGraphicsExtractor graphics) {
        hudBounds.clear();

        for (HudRenderable hud : AriesHudManager.getHudElements()) {
            if (!hud.isHudEnabled()) {
                continue;
            }

            HudPosition position = hud.getHudPosition();

            HudBounds bounds = hud.renderHud(graphics, position.getX(), position.getY());
            hudBounds.put(hud, bounds);
        }

        if (selectedHud != null) {
            HudBounds bounds = hudBounds.get(selectedHud);

            if (bounds != null) {
                graphics.fill(
                    bounds.x(), bounds.y(),
                    bounds.x() + bounds.width(),
                    bounds.y() + bounds.height(),
                    0x33FFFFFF
                );

                HudPosition position = selectedHud.getHudPosition();

                selectedHud.renderHud(
                    graphics,
                    position.getX(),
                    position.getY()
                );

                renderSelectedHudInfo(graphics, bounds);
            }
        }

    }

    private void renderSelectedHudInfo(GuiGraphicsExtractor graphics, HudBounds bounds) {
        GuiRegions region = getHudRegion(bounds);

        if (region == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        String info = String.format(
            "x:%d, y:%d, %s",
            bounds.x(), bounds.y(),
            region.name().toLowerCase(Locale.ROOT)
        );

        int infoWidth = minecraft.font.width(info);
        int textHeight = minecraft.font.lineHeight;

        int textX;

        switch (region) {
            case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> textX = bounds.x();
            case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> textX = bounds.x() + bounds.width() - infoWidth;
            default -> textX = bounds.x() + (bounds.width() - infoWidth) / 2;
        }

        int textY;
        int gap = (int) (AriesScreen.PADDING / 2.5);

        if (bounds.y() - gap - textHeight >= 0) {
            textY = bounds.y() - gap - textHeight;
        } else  {
            textY = bounds.y() + bounds.height() + gap;
        }

        graphics.text(minecraft.font, info, textX, textY, 0xFFFFFFFF);

    }

    private GuiRegions getHudRegion(HudBounds bounds) {
        double x = bounds.x();
        double y = bounds.y();

        for (GuiRegions region : GuiRegions.values()) {
            GuiBounds screenBounds = region.getRegion().toScreenBounds(width, height);

            if (screenBounds.contains(x, y)) {
                return region;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick);
        }

        for (HudRenderable hud : AriesHudManager.getHudElements()) {
            if (!hud.isHudEnabled()) {
                continue;
            }

            HudBounds bounds = hudBounds.get(hud);

            if (bounds == null) {
                continue;
            }

            HudPosition position = hud.getHudPosition();

            if (ScreenHelper.isHovered(
                event.x(), event.y(),
                bounds.x(), bounds.y(),
                bounds.width(), bounds.height()
            )) {
                selectedHud = hud;
                draggableHud = hud;

                dragOffsetX = (int) event.x() - position.getX();
                dragOffsetY = (int) event.y() - position.getY();

                return true;
            }
        }

        selectedHud = null;

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        if (draggableHud == null || event.button() != 0) {
            return super.mouseDragged(event, dragX, dragY);
        }

        HudPosition position = draggableHud.getHudPosition();
        HudBounds bounds = hudBounds.get(draggableHud);

        if (bounds == null) {
            return false;
        }

        int newX = (int) event.x() - dragOffsetX;
        int newY = (int) event.y() - dragOffsetY;

        newX = Math.clamp(newX, 0, width - bounds.width());
        newY = Math.clamp(newY, 0, height - bounds.height());

        position.setPosition(newX, newY);

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && draggableHud != null) {
            draggableHud = null;
            return true;
        }

        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        AriesConfig.save();

        //? if 26.2
        minecraft.gui.setScreen(parent);
        //? if 26.1.2
        //minecraft.setScreen(parent);
    }
}

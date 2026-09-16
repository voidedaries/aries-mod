package dev.voidedaries.aries.client.gui.location;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface HudRenderable {

    String getHudId();

    boolean isHudEnabled();

    HudPosition getHudPosition();

    default float getHudScale() {
        return 1.0f;
    }

    HudBounds renderHud(GuiGraphicsExtractor graphicsExtractor, int x, int y);

}

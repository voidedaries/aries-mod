package dev.voidedaries.aries.client.gui.location;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface HudRenderable {

    String getHudId();

    boolean isHudEnabled();

    void setHudEnabled(boolean enabled);

    HudPosition getHudPosition();

    HudBounds renderHud(GuiGraphicsExtractor graphicsExtractor, int x, int y);

    HudBounds getHudBounds();

}

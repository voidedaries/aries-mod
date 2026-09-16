package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import dev.voidedaries.aries.client.gui.location.HudBounds;
import dev.voidedaries.aries.client.gui.location.HudPosition;
import dev.voidedaries.aries.client.gui.location.HudRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class FPSDisplayFeature extends AriesFeature implements HudRenderable {
    public final BooleanConfig enabled = addConfig(
        new BooleanConfig("fps_display.enabled", false)
    );

    private final HudPosition hudPosition = new HudPosition();

    public FPSDisplayFeature() {
        super(
            Component.translatable("gui.category.misc.fps_display.name"),
            Component.translatable("gui.category.misc.fps_display.description"),
            AriesCategory.MISC
        );
    }

    @Override
    public String getHudId() {
        return "fps_display";
    }

    @Override
    public boolean isHudEnabled() {
        return enabled.get();
    }

    @Override
    public HudPosition getHudPosition() {
        return hudPosition;
    }

    @Override
    public HudBounds renderHud(GuiGraphicsExtractor graphics, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();

        String text = minecraft.getFps() + " FPS";

        graphics.text(minecraft.font, text, x, y, 0xFFFFFFFF);

        return new HudBounds(x, y, minecraft.font.width(text), minecraft.font.lineHeight);
    }
}

package dev.voidedaries.aries.client.client.gui;

import dev.voidedaries.aries.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class AriesScreen extends Screen {
    private static final int MENU_WIDTH = 360;
    private static final int MENU_HEIGHT = 240;

    private int menuX;
    private int menuY;

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
        menuX = (this.width - MENU_WIDTH) / 2;
        menuY = (this.height - MENU_HEIGHT) / 2;
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        graphics.fill(
                menuX,
                menuY,
                menuX + MENU_WIDTH,
                menuY + MENU_HEIGHT,
                0xFF232833
        );

    }

}

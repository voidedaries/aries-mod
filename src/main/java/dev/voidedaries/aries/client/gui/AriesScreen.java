package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.category.AriesCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public class AriesScreen extends Screen {
    private static final int MENU_WIDTH = 360;
    private static final int MENU_HEIGHT = 220;

    private static final int PADDING = 10;

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        int x = (this.width - getMenuWidth()) / 2;
        int y = (this.height - getMenuHeight()) / 2;

        graphics.fill(x, y, x + getMenuWidth(), y + getMenuHeight(), 0xFF222933);

        graphics.fill(x, y, x + getCategoryWidth(), y + getMenuHeight(), 0xFF151A21);

        drawCategories(graphics, mouseX, mouseY, delta);

    }

    private void drawCategories(@NonNull GuiGraphics graphics, int ignoredMouseX, int ignoredMouseY, float ignoredDelta) {
        int x = (this.width - getMenuWidth()) / 2;
        int y = (this.height - getMenuHeight()) / 2;

        int startCategoryHeight = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        graphics.drawCenteredString(
                this.font,
                Component.translatable("gui.menu.categories"),
                x + (getCategoryWidth() / 2),
                y + PADDING,
                0xFF0058E1
        );

        graphics.hLine(
                x + PADDING,
                x + getCategoryWidth() - PADDING,
                y + PADDING + this.font.lineHeight + PADDING / 2,
                0xFF2D3642
        );


        for (AriesCategory category : AriesCategory.values()) {
            graphics.drawCenteredString(
                    this.font,
                    category.getName(),
                    x + (getCategoryWidth() / 2),
                    startCategoryHeight,
                    0xFFADB5C9
            );

            startCategoryHeight += this.font.lineHeight + PADDING;
        }
    }

    private float getScale() {
        return Mth.clamp(Math.min(this.width / 1920f, this.height / 1080f), 1.0f, 2.0f);
    }

    private int getCategoryWidth() {
        return (int) (getMenuWidth() / 3.5);
    }

    private int getMenuWidth() {
        return (int) (MENU_WIDTH * getScale());
    }

    private int getMenuHeight() {
        return (int) (MENU_HEIGHT * getScale());
    }

}

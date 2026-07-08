package dev.voidedaries.aries.client.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.config.render.ConfigInteraction;
import dev.voidedaries.aries.client.feature.config.render.ConfigTypeRenderer;
import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.config.types.BooleanConfig;
import dev.voidedaries.aries.client.feature.config.types.IntConfig;
import dev.voidedaries.aries.client.feature.config.types.SliderConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AriesScreen extends Screen {
    private AriesCategory selectedCategory = AriesCategory.ABOUT; // first category on first opening

    private static final int MENU_WIDTH = 360;
    private static final int MENU_HEIGHT = 220;
    private static final int PADDING = 10;

    private ConfigInteraction activeSlider = null;

    private final List<ConfigInteraction> configTypeInteractions = new ArrayList<>();

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
        configTypeInteractions.clear();

        selectedCategory = AriesUIState.lastCategory;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        configTypeInteractions.clear();

        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        graphics.fill(x, y, x + getMenuWidth(), y + getMenuHeight(), 0xFF222933);
        graphics.fill(x, y, x + getCategoryWidth(), y + getMenuHeight(), 0xFF151A21);

        drawCategories(graphics, mouseX, mouseY, delta);
        drawMenu(graphics, mouseX, mouseY, delta);

        for (ConfigInteraction interaction : configTypeInteractions) {
            if (ScreenHelper.isHovered(
                mouseX, mouseY,
                interaction.x(), interaction.y(),
                interaction.width(), interaction.height()
            )) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
                break;
            }
        }
    }

    private void drawMenu(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int contentX = x + getCategoryWidth() + PADDING * 2;
        int contentY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        AriesCategory currentCategory = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        boolean authorHovered = isAuthorHovered(mouseX, mouseY);

        if (authorHovered) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }

        graphics.centeredText(
            this.font,
            Component.literal("Aries")
                .append(Component.literal(" • ").withColor(0xFFADB5C9))
                .append(Component.translatable("authors.dev.voidedaries")
                    .withStyle(s -> authorHovered ? s.withUnderlined(true) : s))
                .append(Component.literal(" • ").withColor(0xFFADB5C9))
                .append(Component.literal(ModConstants.displayVersion)),
            x + getCategoryWidth() + 2 * PADDING,
            y + PADDING,
            0xFF0058E1
        );

        graphics.horizontalLine(
            x + getCategoryWidth() + PADDING,
            x + getMenuWidth() - PADDING,
            y + PADDING + this.font.lineHeight + PADDING / 2,
            0xFF2D3642
        );

        int entryY = contentY;

        // drawing features
        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            if (feature.getCategory() != currentCategory || !feature.isVisible()) {
                continue;
            }

            graphics.text(
                this.font,
                feature.getName(),
                contentX,
                entryY,
                0xFFFFFFFF
            );

            int descMaxWidth = (int) ((getMenuWidth() - getCategoryWidth()) / 1.5);

            List<FormattedCharSequence> descLines = this.font.split(
                Component.literal(feature.getDescription().getString()), descMaxWidth
            );

            // description text
            for (int line = 0; line < descLines.size(); line++) {
                float descScale = 0.8f;
                graphics.pose().pushMatrix();
                graphics.pose().translate(
                    contentX,
                    entryY + this.font.lineHeight + (float) PADDING / 2 + line * this.font.lineHeight
                );
                graphics.pose().scale(descScale, descScale);
                graphics.text(this.font, descLines.get(line), 0, 0, 0xFFADB5C9);
                graphics.pose().popMatrix();
            }

            int controlRightX = x + getMenuWidth() - PADDING * 2;

            // config type rendering
            for (AriesConfigType<?> config : feature.getConfigs()) {
                switch (config.getType()) {
                    case TOGGLE -> configTypeInteractions.add(
                        ConfigTypeRenderer.drawToggle(graphics, this.font, config, controlRightX, entryY)
                    );
                    case SLIDER -> configTypeInteractions.add(
                        ConfigTypeRenderer.drawSlider(graphics, this.font, config, controlRightX, entryY)
                    );
                    case COLOR -> configTypeInteractions.add(
                        ConfigTypeRenderer.drawColorPicker(graphics, this.font, config, controlRightX, entryY)
                    );
                }
            }

            int featureHeight =
                this.font.lineHeight + (int)(descLines.size() * this.font.lineHeight * 0.8f) + PADDING * 2;

            entryY += featureHeight;
        }
    }

    private void drawCategories(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int startCategoryHeight = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        graphics.centeredText(
            this.font,
            Component.translatable("gui.menu.categories"),
            x + (getCategoryWidth() / 2),
            y + PADDING,
            0xFF0058E1
        );

        graphics.horizontalLine(
            x + PADDING,
            x + getCategoryWidth() - PADDING,
            y + PADDING + this.font.lineHeight + PADDING / 2,
            0xFF2D3642
        );

        AriesCategory current = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        int index = 0;

        for (AriesCategory category : AriesCategory.values()) {
            int entryY = startCategoryHeight + index * (this.font.lineHeight + PADDING);
            boolean categoryHovered =
                ScreenHelper.isHovered(mouseX, mouseY, x, entryY, getCategoryWidth(), this.font.lineHeight);

            if (categoryHovered) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }

            int color = (category == current)
                ? 0xFFFFFFFF
                : (categoryHovered ? 0xFFFFFFFF : 0xFFADB5C9);

            graphics.centeredText(
                this.font,
                category.getName(),
                x + (getCategoryWidth() / 2),
                entryY,
                color
            );

            index++;
        }
    }

    private boolean isAuthorHovered(int mouseX, int mouseY) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int authorX = x + getCategoryWidth() + PADDING;
        int authorY = y + PADDING;

        int titleTextWidth = this.font.width("Aries • ");
        int authorWidth = this.font.width(Component.translatable("authors.dev.voidedaries"));

        int hoverX = authorX + titleTextWidth;
        int hoverHeight = this.font.lineHeight;

        return ScreenHelper.isHovered(mouseX, mouseY, hoverX, authorY, authorWidth, hoverHeight);
    }

    private void updateSlider(int mouseX, ConfigInteraction interaction) {
        if (!(interaction.config() instanceof SliderConfig slider)) {
            return;
        }

        int x = interaction.x();
        int width = interaction.width();

        float percent = (mouseX - x) / (float) width;
        percent = Mth.clamp(percent, 0f, 1f);

        slider.setFromPercent(percent);
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

    private float getScale() {
        return Mth.clamp(Math.min(this.width / 1920f, this.height / 1080f), 1.0f, 2.0f);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();
        boolean authorHovered = isAuthorHovered(mouseX, mouseY);

        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }

        if (authorHovered) {
            Util.getPlatform().openUri(ModConstants.GITHUB_URL);
        }

        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int startY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        int i = 0;

        for (AriesCategory category : AriesCategory.values()) {

            int entryY = startY + i * (this.font.lineHeight + PADDING);

            boolean hovered =
                mouseX >= x && mouseX <= x + getCategoryWidth() &&
                    mouseY >= entryY && mouseY <= entryY + this.font.lineHeight;

            if (hovered) {
                selectedCategory = category;
                AriesUIState.lastCategory = category;
                return true;
            }

            i++;
        }

        for (ConfigInteraction interaction : configTypeInteractions) {
            boolean hovered = mouseX >= interaction.x() && mouseX <= interaction.x() + interaction.width()
                && mouseY >= interaction.y() && mouseY <= interaction.y() + interaction.height();

            if (!hovered) {
                continue;
            }

            if (interaction.config() instanceof BooleanConfig bool) {
                bool.set(!bool.get());
                AriesConfig.save();
                return true;
            }

            if (interaction.config() instanceof IntConfig) {
                activeSlider = interaction;
                updateSlider(mouseX, interaction);
                return true;
            }
        }

        return super.mouseClicked(event, handled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (activeSlider != null) {
            updateSlider((int) event.x(), activeSlider);
            return true;
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        activeSlider = null;

        AriesConfig.save();
        return super.mouseReleased(event);
    }

}

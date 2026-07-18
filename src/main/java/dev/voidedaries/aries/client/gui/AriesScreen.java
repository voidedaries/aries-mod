package dev.voidedaries.aries.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.types.*;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.entry.FeatureEntry;
import dev.voidedaries.aries.client.render.feature.ConfigInteraction;
import dev.voidedaries.aries.client.render.feature.ConfigTypeRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
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

    private static final int FEATURE_SPACING = PADDING / 2;
    private static final int ENTRY_SPACING = PADDING / 2;

    private ConfigInteraction activeSlider = null;
    public KeybindConfig listeningKeybind;

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

        graphics.text(
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

        int contentLeft = x + getCategoryWidth() + PADDING;
        int contentTop = contentY - PADDING;
        int contentRight = x + getMenuWidth() - PADDING;
        int contentBottom = y + getMenuHeight();

        ScreenHelper.enableScissor(
            graphics,
            contentLeft,
            contentTop,
            contentRight,
            contentBottom
        );

        // drawing features
        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            if (feature.getCategory() != currentCategory || !feature.isVisible()) {
                continue;
            }

            // Padding from the right of the menu
            int controlRightX = x + getMenuWidth() - PADDING * 2;

            // Main feature title
            graphics.text(
                this.font,
                feature.getName(),
                contentX,
                entryY,
                0xFFFFFFFF
            );

            List<FormattedCharSequence> featureDescription = this.font.split(
                feature.getDescription(), (int) ((getMenuWidth() - getCategoryWidth()) / 1.5)
            );

            int featureDescriptionHeight =
                drawDescription(graphics, featureDescription, contentX, entryY + this.font.lineHeight + PADDING / 2);

            int currentY = entryY;

            int featureTextHeight = this.font.lineHeight + featureDescriptionHeight;

            int featureConfigHeight = getConfigHeight(feature.getConfigs());

            int featureConfigY;

            if (featureConfigHeight <= featureTextHeight) {
                featureConfigY = entryY + (featureTextHeight - featureConfigHeight) / 2;
            } else {
                featureConfigY = entryY;
            }

            for (AriesConfigType<?> config : feature.getConfigs()) {
                if (!config.isVisible()) {
                    continue;
                }

                ConfigInteraction interaction = drawConfigs(graphics, config, controlRightX, featureConfigY);

                if (interaction != null) {
                    configTypeInteractions.add(interaction);

                    featureConfigY += interaction.height() + PADDING * 2;
                }
            }

            currentY += Math.max(featureTextHeight, featureConfigHeight) + FEATURE_SPACING * 4;

            // Sub entries/features
            for (FeatureEntry entry : feature.getEntries()) {
                if (!entry.isVisible()) {
                    continue;
                }

                graphics.text(
                    this.font,
                    entry.name(),
                    contentX,
                    currentY,
                    0xFFFFFFFF
                );

                List<FormattedCharSequence> description = this.font.split(
                    entry.description(),
                    (int)((getMenuWidth() - getCategoryWidth()) / 1.5)
                );

                int descriptionHeight = drawDescription(
                    graphics,
                    description,
                    contentX,
                    currentY + this.font.lineHeight + PADDING / 2
                );

                int textHeight = this.font.lineHeight + descriptionHeight;

                int configHeight = getConfigHeight(entry.configs());

                int configY;

                if (configHeight <= textHeight) {
                    configY = currentY + (textHeight - configHeight) / 2;
                } else {
                    configY = currentY;
                }

                // Config rendering
                for (AriesConfigType<?> config : entry.configs()) {
                    if (!config.isVisible()) {
                        continue;
                    }

                    ConfigInteraction interaction = drawConfigs(graphics, config, controlRightX, configY);

                    if (interaction != null) {
                        configTypeInteractions.add(interaction);

                        configY += interaction.height() + PADDING * 2;
                    }
                }

                currentY += Math.max(textHeight, configHeight) + ENTRY_SPACING * 4;
            }

            entryY = currentY + FEATURE_SPACING;
        }

        ScreenHelper.disableScissor(graphics);
    }

    private int getConfigHeight(List<AriesConfigType<?>> configs) {
        int height = 0;

        for (AriesConfigType<?> config : configs) {
            height += ConfigTypeRenderer.getConfigHeight(config) + PADDING * 2;
        }

        if (height > 0) {
            height -= PADDING * 2;
        }

        return height;
    }

    private ConfigInteraction drawConfigs(
        GuiGraphicsExtractor graphics,
        AriesConfigType<?> config,
        int controlRightX,
        int configY
    ) {
        return switch (config.getType()) {

            case LIST, BUTTON, TEXT -> null;

            case TOGGLE ->
                ConfigTypeRenderer.drawToggle(graphics, this.font, config, controlRightX, configY);

            case SLIDER ->
                ConfigTypeRenderer.drawSlider(graphics, this.font, config, controlRightX, configY);

            case COLOR ->
                ConfigTypeRenderer.drawColorPicker(graphics, this.font, config, controlRightX, configY);

            case KEYBIND -> {
                KeybindConfig keybind = (KeybindConfig) config;

                yield ConfigTypeRenderer.drawKeybind(
                    graphics, this.font, config, controlRightX, configY, listeningKeybind == keybind
                );
            }
        };
    }

    private int drawDescription(GuiGraphicsExtractor graphics, List<FormattedCharSequence> lines, int x, int y) {
        float scale = 0.8f;

        // description text
        for (int line = 0; line < lines.size(); line++) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(
                x,
                y + line * this.font.lineHeight
            );
            graphics.pose().scale(scale, scale);
            graphics.text(this.font, lines.get(line), 0, 0, 0xFFADB5C9);
            graphics.pose().popMatrix();
        }

        return (int) (lines.size() * this.font.lineHeight * scale);
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
        if (!(interaction.config() instanceof SliderValue slider)) {
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
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (listeningKeybind != null) {

            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                listeningKeybind.set(InputConstants.UNKNOWN.getValue());
                AriesConfig.save();

                listeningKeybind = null;
                return true;
            }

            listeningKeybind.set(event.key());
            AriesConfig.save();

            listeningKeybind = null;
            return true;
        }

        return super.keyPressed(event);
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

            if (interaction.config() instanceof SliderValue) {
                activeSlider = interaction;
                updateSlider(mouseX, interaction);
                return true;
            }

            if (interaction.config() instanceof KeybindConfig keybind) {
                listeningKeybind = keybind;
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

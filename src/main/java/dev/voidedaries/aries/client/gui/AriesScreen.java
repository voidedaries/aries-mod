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
import net.minecraft.client.input.CharacterEvent;
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

    //menu
    static final int MENU_WIDTH = 360;
    static final int MENU_HEIGHT = 220;
    static final int PADDING = 10;

    private static final int FEATURE_SPACING = PADDING / 2;
    private static final int ENTRY_SPACING = PADDING / 2;

    //content
    private int contentHeight;

    //scrollbar
    private static final int SCROLLBAR_WIDTH = 4;
    private int scrollOffset = 0;
    private boolean draggingScrollbar = false;
    private int scrollbarOffset = 0;

    //interaction
    private ConfigInteraction activeSlider = null;
    public KeybindConfig listeningKeybind;

    //config states
    private SliderEditState editingSlider;

    private final List<ConfigInteraction> configTypeInteractions = new ArrayList<>();

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
        configTypeInteractions.clear();

        selectedCategory = AriesScreenHelper.lastCategory;
        scrollOffset = AriesScreenHelper.savedScrollPosition;
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

        //Scrollbar thumb hover
        int scrollbarX = x + getMenuWidth() - PADDING - SCROLLBAR_WIDTH;

        int visibleHeight =
            AriesScreenHelper.getContentVisibleHeight(this.height, getMenuHeight(), PADDING, this.font.lineHeight);

        int scrollbarHeight = AriesScreenHelper.calculateScrollbarHeight(visibleHeight, PADDING);
        int thumbHeight = AriesScreenHelper.calculateThumbHeight(scrollbarHeight, contentHeight, visibleHeight);

        int thumbY = AriesScreenHelper.calculateThumbY(
            AriesScreenHelper.getScrollbarTop(
                this.height,
                getMenuHeight(),
                PADDING,
                this.font.lineHeight
            ),
            scrollbarHeight,
            thumbHeight,
            scrollOffset,
            contentHeight,
            visibleHeight
        );

        if (ScreenHelper.isHovered(
            mouseX,
            mouseY,
            scrollbarX,
            thumbY,
            SCROLLBAR_WIDTH,
            thumbHeight
        )) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
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

        int visibleHeight = contentBottom - contentTop;

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
            int controlRightX = (int) (x + getMenuWidth() - PADDING * 2.5);
            int drawY = entryY - scrollOffset;

            // Main feature title
            graphics.text(
                this.font,
                feature.getName(),
                contentX,
                drawY,
                0xFFFFFFFF
            );

            List<FormattedCharSequence> featureDescription = this.font.split(
                feature.getDescription(), (int) ((getMenuWidth() - getCategoryWidth()) / 1.5)
            );

            int featureDescriptionHeight =
                drawDescription(graphics, featureDescription, contentX, drawY + this.font.lineHeight + PADDING / 2);

            int currentY = entryY;

            int featureTextHeight = this.font.lineHeight + featureDescriptionHeight;

            int featureConfigHeight = getConfigHeight(feature.getConfigs());

            int featureConfigY;

            if (featureConfigHeight <= featureTextHeight) {
                featureConfigY = drawY + (featureTextHeight - featureConfigHeight) / 2;
            } else {
                featureConfigY = drawY;
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

                int entryDrawY = currentY - scrollOffset;

                graphics.text(
                    this.font,
                    entry.name(),
                    contentX,
                    entryDrawY,
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
                    entryDrawY + this.font.lineHeight + PADDING / 2
                );

                int textHeight = this.font.lineHeight + descriptionHeight;

                int configHeight = getConfigHeight(entry.configs());

                int configY;

                if (configHeight <= textHeight) {
                    configY = entryDrawY + (textHeight - configHeight) / 2;
                } else {
                    configY = entryDrawY;
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

        contentHeight = entryY - contentY;
        scrollOffset = AriesScreenHelper.clampScroll(scrollOffset, contentHeight, visibleHeight);
        boolean needsScrollbar = contentHeight > visibleHeight;

        ScreenHelper.disableScissor(graphics);

        if (needsScrollbar) {
            drawScrollbar(
                graphics,
                contentRight - SCROLLBAR_WIDTH,
                contentTop + PADDING,
                (contentBottom - contentTop) - (PADDING * 2),
                visibleHeight
            );
        }
    }

    private void drawScrollbar(GuiGraphicsExtractor graphics, int x, int y, int height, int visibleHeight) {
        // track
        graphics.fill(x, y, x + SCROLLBAR_WIDTH, y + height, 0xFF151A21);

        int thumbHeight = AriesScreenHelper.calculateThumbHeight(height, contentHeight, visibleHeight);

        int thumbY =
            AriesScreenHelper.calculateThumbY(y, height, thumbHeight, scrollOffset, contentHeight, visibleHeight);

        // thumb
        graphics.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFF0058E1);
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
                ConfigTypeRenderer.drawSlider(graphics, this.font, config, controlRightX, configY, editingSlider);

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
    public void removed() {
        AriesScreenHelper.savedScrollPosition = scrollOffset;
        AriesScreenHelper.lastCategory = selectedCategory;

        super.removed();
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {

        if (editingSlider != null) {
            int codePoint = event.codepoint();

            if (Character.isDigit(codePoint)
                || codePoint == '.'
                || codePoint == '-') {
                editingSlider.addChar((char) codePoint);
            }

            return true;
        }

        return super.charTyped(event);
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

        if (editingSlider != null) {
            switch (event.key()) {
                case GLFW.GLFW_KEY_ESCAPE -> {
                    editingSlider = null;
                    return true;
                }

                case GLFW.GLFW_KEY_ENTER -> {
                    if (editingSlider.apply()) {
                        AriesConfig.save();
                    }

                    editingSlider = null;
                    return true;
                }

                case GLFW.GLFW_KEY_BACKSPACE -> {
                    editingSlider.backspace();
                    return true;
                }
            }

            return true;
        }

        if (activeSlider != null && activeSlider.config() instanceof SliderValue slider) {
            int amount = ((event.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0) ? 10 : 1;

            switch (event.key()) {
                case GLFW.GLFW_KEY_LEFT -> {
                    slider.step(-amount);
                    AriesConfig.save();
                    return true;
                }

                case GLFW.GLFW_KEY_RIGHT -> {
                    slider.step(amount);
                    AriesConfig.save();
                    return true;
                }
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();
        boolean authorHovered = isAuthorHovered(mouseX, mouseY);

        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT && event.button() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return false;
        }

        if (authorHovered) {
            Util.getPlatform().openUri(ModConstants.GITHUB_URL);
        }

        if (editingSlider != null) {
            editingSlider = null;
        }

        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int scrollbarX = x + getMenuWidth() - PADDING - SCROLLBAR_WIDTH;

        int visibleHeight =
            AriesScreenHelper.getContentVisibleHeight(this.height, getMenuHeight(), PADDING, this.font.lineHeight);

        int scrollbarHeight = AriesScreenHelper.calculateScrollbarHeight(visibleHeight, PADDING);

        int thumbHeight = AriesScreenHelper.calculateThumbHeight(
            scrollbarHeight,
            contentHeight,
            visibleHeight
        );

        int thumbY = AriesScreenHelper.calculateThumbY(
            AriesScreenHelper.getScrollbarTop(
                this.height,
                getMenuHeight(),
                PADDING,
                this.font.lineHeight
            ),
            scrollbarHeight,
            thumbHeight,
            scrollOffset,
            contentHeight,
            visibleHeight
        );

        int scrollbarHitboxWidth = 12;

        if (ScreenHelper.isHovered(mouseX, mouseY,
            scrollbarX - (scrollbarHitboxWidth - SCROLLBAR_WIDTH) / 2, thumbY,
            scrollbarHitboxWidth,
            thumbHeight)) {
            draggingScrollbar = true;
            scrollbarOffset = mouseY - thumbY;

            return true;
        }

        int startY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        int i = 0;

        for (AriesCategory category : AriesCategory.values()) {

            int entryY = startY + i * (this.font.lineHeight + PADDING);

            boolean hovered =
                mouseX >= x && mouseX <= x + getCategoryWidth() &&
                    mouseY >= entryY && mouseY <= entryY + this.font.lineHeight;

            if (hovered) {
                if (selectedCategory != category) {
                    selectedCategory = category;
                    AriesScreenHelper.lastCategory = category;

                    scrollOffset = 0;
                    AriesScreenHelper.savedScrollPosition = 0;
                }

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

            if (interaction.config() instanceof SliderValue slider) {
                if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    editingSlider = new SliderEditState(slider);
                    return true;
                }

                if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    activeSlider = interaction;
                    updateSlider(mouseX, interaction);
                    return true;
                }
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
        if (draggingScrollbar) {
            int visibleHeight =
                AriesScreenHelper.getContentVisibleHeight(this.height, getMenuHeight(), PADDING, this.font.lineHeight);

            int scrollbarHeight = AriesScreenHelper.calculateScrollbarHeight(
                visibleHeight,
                PADDING
            );

            int thumbHeight = AriesScreenHelper.calculateThumbHeight(
                scrollbarHeight,
                contentHeight,
                visibleHeight
            );

            scrollOffset = AriesScreenHelper.calculateScrollOffsetFromDrag(
                event.y(),
                scrollbarOffset,
                AriesScreenHelper.getScrollbarTop(this.height, getMenuHeight(), PADDING, this.font.lineHeight),
                scrollbarHeight,
                thumbHeight,
                contentHeight,
                visibleHeight
            );

            scrollOffset = AriesScreenHelper.clampScroll(
                scrollOffset,
                contentHeight,
                visibleHeight
            );

            AriesScreenHelper.savedScrollPosition = scrollOffset;

            return true;
        }

        if (activeSlider != null) {
            updateSlider((int) event.x(), activeSlider);
            return true;
        }

        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        activeSlider = null;
        draggingScrollbar = false;

        AriesConfig.save();
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int visibleHeight =
            AriesScreenHelper.getContentVisibleHeight(this.height, getMenuHeight(), PADDING, this.font.lineHeight);

        boolean menuBounds = ScreenHelper.isHovered(
            (int) mouseX,
            (int) mouseY,
            x + getCategoryWidth(),
            AriesScreenHelper.getScrollbarTop(
                this.height,
                getMenuHeight(),
                PADDING,
                this.font.lineHeight
            ),
            getMenuWidth() - getCategoryWidth() - PADDING,
            getMenuHeight() - (
                AriesScreenHelper.getScrollbarTop(
                    this.height,
                    getMenuHeight(),
                    PADDING,
                    this.font.lineHeight
                ) - y
            )
        );

        if (!menuBounds) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        scrollOffset -= (int) (scrollY * 15);

        scrollOffset = AriesScreenHelper.clampScroll(scrollOffset, contentHeight, visibleHeight);
        AriesScreenHelper.savedScrollPosition = scrollOffset;

        return true;
    }
}

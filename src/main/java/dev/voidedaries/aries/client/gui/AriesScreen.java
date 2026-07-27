package dev.voidedaries.aries.client.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.entry.FeatureEntry;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.types.KeybindConfig;
import dev.voidedaries.aries.client.feature.types.interaction.*;
import dev.voidedaries.aries.client.render.feature.ConfigInteraction;
import dev.voidedaries.aries.client.render.feature.ConfigTypeRenderer;
import dev.voidedaries.aries.client.render.feature.OpenColorPicker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AriesScreen extends Screen {
    private AriesCategory selectedCategory = AriesCategory.ABOUT; // first category on first opening

    // menu
    static final int MENU_WIDTH = 360;
    static final int MENU_HEIGHT = 220;
    public static final int PADDING = 10;

    private static final int FEATURE_SPACING = PADDING / 2;
    private static final int ENTRY_SPACING = PADDING / 2;

    // content
    private int contentHeight;

    // scrollbar
    static final int SCROLLBAR_WIDTH = 4;
    private int scrollOffset = 0;

    // mouse & keyboard handlers
    private AriesScreenKeyboardHandling keyboardHandling;
    private AriesScreenMouseHandling mouseHandling;

    //interaction
    private ConfigInteraction activeSlider = null;
    public KeybindConfig listeningKeybind;

    //config states
    private EditState editingState;
    private OpenColorPicker activeColorPicker;

    public static int COLOR_PICKER_BOX_WIDTH = 120;
    public static int COLOR_PICKER_BOX_HEIGHT = 110;

    private final List<ConfigInteraction> configTypeInteractions = new ArrayList<>();

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
        //setters for menu
        configTypeInteractions.clear();

        keyboardHandling = new AriesScreenKeyboardHandling(this);
        mouseHandling = new AriesScreenMouseHandling(this);
        selectedCategory = AriesScreenCache.category;
        scrollOffset = AriesScreenCache.savedScrollPosition;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        configTypeInteractions.clear();

        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());


        //drawing menu & categories
        graphics.fill(x, y, x + getMenuWidth(), y + getMenuHeight(), 0xFF222933);
        graphics.fill(x, y, x + getCategoryWidth(), y + getMenuHeight(), 0xFF151A21);

        drawCategories(graphics, mouseX, mouseY, delta);
        drawMenu(graphics, mouseX, mouseY, delta);

        updateCursor(graphics, mouseX, mouseY);
    }

    private void drawMenu(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        // starting position for the main content area
        int contentX = x + getCategoryWidth() + PADDING * 2;

        // starting position below the menu header/title
        int contentY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);
        int entryContentY = contentY;

        AriesCategory currentCategory = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        //author
        graphics.text(
            this.font,
            Component.literal("Aries")
                .append(Component.literal(" • ").withColor(0xFFADB5C9))
                .append(Component.translatable("authors.dev.voidedaries")
                    .withStyle(s -> isAuthorHovered(mouseX, mouseY) ? s.withUnderlined(true) : s))
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

        // scissor bounds
        int contentLeft = x + getCategoryWidth() + PADDING;
        int contentTop = contentY - PADDING;
        int contentRight = x + getMenuWidth() - PADDING;
        int contentBottom = y + getMenuHeight();

        int visibleHeight = contentBottom - contentTop;

        //bounds for rendering features and configs
        graphics.enableScissor(
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

            // padding from the right of the menu
            int configRenderX = (int) (x + getMenuWidth() - PADDING * 2.5);
            // translating content position into render position
            int renderY = entryContentY - scrollOffset;

            // feature title
            graphics.text(
                this.font,
                feature.getName(),
                contentX,
                renderY,
                0xFFFFFFFF
            );

            List<FormattedCharSequence> featureDescription = this.font.split(
                feature.getDescription(), (int) ((getMenuWidth() - getCategoryWidth()) / 1.5)
            );

            int featureDescriptionHeight =
                drawDescription(graphics, featureDescription, contentX, renderY + this.font.lineHeight + PADDING / 2);

            int featureTextHeight = this.font.lineHeight + featureDescriptionHeight;

            int featureConfigHeight = getConfigHeight(feature.getConfigs());

            int featureConfigY;

            if (featureConfigHeight <= featureTextHeight) {
                featureConfigY = renderY + (featureTextHeight - featureConfigHeight) / 2;
            } else {
                featureConfigY = renderY;
            }

            // feature config interactions
            for (AriesConfigType<?> config : feature.getConfigs()) {
                if (!config.isVisible()) {
                    continue;
                }

                List<ConfigInteraction> interactions =
                    drawConfigs(graphics, config,  configRenderX, featureConfigY, mouseX, mouseY);

                if (interactions != null) {
                    configTypeInteractions.addAll(interactions);

                    if (!interactions.isEmpty()) {
                        int maxHeight = interactions.stream().mapToInt(ConfigInteraction::height).max().orElse(0);
                        featureConfigY += maxHeight + PADDING * 2;
                    }
                }
            }

            // tracks the next available vertical position in the content layout
            int nextContentY = entryContentY;
            nextContentY += Math.max(featureTextHeight, featureConfigHeight) + FEATURE_SPACING * 4;

            // drawing entries for feature specific sub-configs
            for (FeatureEntry entry : feature.getEntries()) {
                if (!entry.isVisible()) {
                    continue;
                }

                int entryDrawY = nextContentY - scrollOffset;

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

                // calculate config render position based on available text height
                int configRenderY;

                if (configHeight <= textHeight) {
                    configRenderY = entryDrawY + (textHeight - configHeight) / 2;
                } else {
                    configRenderY = entryDrawY;
                }

                // entry config rendering
                for (AriesConfigType<?> config : entry.configs()) {
                    if (!config.isVisible()) {
                        continue;
                    }

                    List<ConfigInteraction> interactions =
                        drawConfigs(graphics, config,  configRenderX, configRenderY, mouseX, mouseY);

                    if (interactions != null) {
                        configTypeInteractions.addAll(interactions);

                        if (!interactions.isEmpty()) {
                            int maxHeight =
                                interactions.stream().mapToInt(ConfigInteraction::height).max().orElse(0);
                            configRenderY += maxHeight + PADDING * 2;
                        }
                    }
                }

                // move the layout cursor down by the entry's height
                nextContentY += Math.max(textHeight, configHeight) + ENTRY_SPACING * 4;
            }

            // position entries below the feature
            entryContentY = nextContentY + FEATURE_SPACING;
        }

        // calculate total scrollable content height
        contentHeight = entryContentY - contentY;
        scrollOffset = AriesScreenLayout.clampScroll(scrollOffset, contentHeight, visibleHeight);

        graphics.disableScissor();

        boolean needsScrollbar = contentHeight > visibleHeight;
        // scrollbar handling
        if (needsScrollbar) {
            drawScrollbar(
                graphics,
                contentRight - SCROLLBAR_WIDTH,
                contentTop + PADDING,
                (contentBottom - contentTop) - (PADDING * 2),
                visibleHeight
            );
        }

        // color picker handling
        if (activeColorPicker != null) {
            drawExpandedColorPicker(graphics, activeColorPicker, mouseX, mouseY);
        }
    }

    //method for drawing descriptions needed for features & entries (separate method for scaling purposes)
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

    private void drawExpandedColorPicker(
        GuiGraphicsExtractor graphics,
        OpenColorPicker picker,
        int mouseX,
        int mouseY
    ) {
        ColorPickerState state = picker.getState();
        int centerX = picker.getX();
        int topY = picker.getY();

        int width = picker.getWidth();
        int height = picker.getHeight();

        int padding = PADDING / 2;

        //triangle for box
        int triangleSize = 12;
        int triangleHeight = triangleSize / 2;

        int boxX = centerX - width / 2;
        int boxY = topY + triangleHeight;
        boxY--;

        int startY = boxY + (PADDING * 2);

        //hsv box
        int hsvBoxX = boxX + padding;
        int hsvBoxY = (int) (startY - (PADDING * 1.25));

        int hsvBoxWidth = width - (padding * 2);
        int hsvBoxHeight = (int) (COLOR_PICKER_BOX_HEIGHT / 2.75);

        int hueColor = Color.HSBtoRGB(state.getHue(), 1.0f, 1.0f);

        int triangleOffset = 2;
        int hueBarY = (int) (hsvBoxY + hsvBoxHeight + (PADDING / 1.5));
        int alphaBarY = hueBarY + (PADDING * 2);

        // Rectangle
        graphics.fill(boxX, boxY, boxX + width, boxY + height, 0xFF434E5B);
        graphics.fill(boxX + 1, boxY + 1, boxX + width - 1, boxY + height - 1, 0xFF222933);

        // Outer triangle
        ScreenHelper.drawTriangle(
            graphics, centerX, topY, triangleSize, 0xFF434E5B, ScreenHelper.TriangleDirection.UP
        );

        // Inner triangle
        ScreenHelper.drawTriangle(
            graphics, centerX, topY + 1, 10, 0xFF222933, ScreenHelper.TriangleDirection.UP
        );

        AriesScreenLayout.drawHSVBox(
            graphics, hsvBoxX, hsvBoxY, hsvBoxWidth, hsvBoxHeight, hueColor
        );

        // hsv indicator selector
        int selectorX = hsvBoxX + (int) (state.getSaturation() * hsvBoxWidth);
        int selectorY = hsvBoxY + (int) ((1 - state.getBrightness()) * hsvBoxHeight);

        graphics.fill(selectorX - 3, selectorY - 3, selectorX + 4, selectorY + 4, 0xFF000000);
        graphics.fill(selectorX - 2, selectorY - 2, selectorX + 3, selectorY + 3, 0xFFFFFFFF);

        // triangle bar indicators (for alpha and hue bars)
        int barTriangleSize = 8;
        int hueSelectorX = hsvBoxX + (int)(state.getHue() * hsvBoxWidth);

        // hue
        ScreenHelper.drawTriangle(
            graphics,
            hueSelectorX,
            hueBarY - triangleHeight + triangleOffset,
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.DOWN
        );

        ScreenHelper.drawTriangle(
            graphics,
            hueSelectorX,
            hueBarY + (int)(padding * 1.75),
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.UP
        );

        AriesScreenLayout.drawHueBar(graphics, hsvBoxX, hueBarY, hsvBoxWidth, (int) (padding * 1.75));

        // alpha
        int alphaSelectorX = hsvBoxX + (int)(state.getAlpha() * hsvBoxWidth);

        ScreenHelper.drawTriangle(
            graphics,
            alphaSelectorX,
            alphaBarY - triangleHeight + triangleOffset,
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.DOWN
        );

        ScreenHelper.drawTriangle(
            graphics,
            alphaSelectorX,
            alphaBarY + (padding * 2) - triangleOffset,
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.UP
        );

        AriesScreenLayout.drawAlphaBar(
            graphics, hsvBoxX, alphaBarY, hsvBoxWidth, (int) (padding * 1.75), 0xFFFFFFFF
        );

        int buttonY = (alphaBarY + (padding * 3));
        int buttonHeight = 16;

        int buttonGap = 4;
        int buttonWidth = (hsvBoxWidth - buttonGap * 2) / 3;

        int pasteX = hsvBoxX + buttonWidth + buttonGap;
        int resetX = pasteX + buttonWidth + buttonGap;

        boolean copyHovered = ScreenHelper.isOverButton(mouseX, mouseY, hsvBoxX, buttonY, buttonWidth, buttonHeight);
        ScreenHelper.drawButton(graphics, this.font, hsvBoxX, buttonY, buttonWidth, buttonHeight, Component.translatable("gui.menu.color_picker.copy"), copyHovered);

        boolean pasteHovered = ScreenHelper.isOverButton(mouseX, mouseY, pasteX, buttonY, buttonWidth, buttonHeight);
        ScreenHelper.drawButton(graphics, this.font, pasteX, buttonY, buttonWidth, buttonHeight, Component.translatable("gui.menu.color_picker.paste"), pasteHovered);

        boolean resetHovered = ScreenHelper.isOverButton(mouseX, mouseY, resetX, buttonY, buttonWidth, buttonHeight);
        ScreenHelper.drawButton(graphics, this.font, resetX, buttonY, buttonWidth, buttonHeight, Component.translatable("gui.menu.color_picker.reset"), resetHovered);
    }

    private void drawScrollbar(GuiGraphicsExtractor graphics, int x, int y, int height, int visibleHeight) {
        // track
        graphics.fill(x, y, x + SCROLLBAR_WIDTH, y + height, 0xFF151A21);

        int thumbHeight = AriesScreenLayout.calculateThumbHeight(height, contentHeight, visibleHeight);

        int thumbY =
            AriesScreenLayout.calculateThumbY(y, height, thumbHeight, scrollOffset, contentHeight, visibleHeight);

        // thumb
        graphics.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFF0058E1);
    }

    //draws configs e.g. drawer methods within ConfigTypeRenderer e.g. toggles/color pickers etc. needed for drawMenu
    private List<ConfigInteraction> drawConfigs(
        GuiGraphicsExtractor graphics,
        AriesConfigType<?> config,
        int controlRightX,
        int configY,
        int mouseX,
        int mouseY
    ) {
        return switch (config.getType()) {

            case LIST, BUTTON, TEXT -> List.of();

            case TOGGLE ->
                List.of(ConfigTypeRenderer.drawToggle(graphics, this.font, config, controlRightX, configY));

            case SLIDER ->
                List.of(ConfigTypeRenderer.drawSlider(
                    graphics,
                    this.font,
                    config,
                    controlRightX,
                    configY,
                    editingState instanceof SliderEditState sliderEdit ? sliderEdit : null
                ));

            case COLOR -> ConfigTypeRenderer.drawColorPicker(
                graphics,
                this.font,
                config,
                controlRightX,
                configY,
                mouseX, mouseY,
                editingState instanceof ColorEditState colorEdit ? colorEdit : null,
                activeColorPicker != null && activeColorPicker.getConfig() == config ? activeColorPicker.getState() : null,
                state -> editingState = state,
                color -> activeColorPicker = color
            );

            case KEYBIND -> {
                KeybindConfig keybind = (KeybindConfig) config;

                yield List.of(ConfigTypeRenderer.drawKeybind(
                    graphics,
                    this.font,
                    config,
                    controlRightX,
                    configY,
                    listeningKeybind == keybind,
                    selected -> listeningKeybind = selected
                ));
            }
        };
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

    //menu hovering options
    private void updateCursor(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY
    ) {
        if (isOverOpenColorPickerControl(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverColorPickerButton(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverConfigInteraction(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverScrollbar(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }

        if (isAuthorHovered(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    private boolean isOverColorPickerButton(int mouseX, int mouseY) {
        OpenColorPicker colorPicker = getActiveColorPicker();

        if (colorPicker == null) {
            return false;
        }

        int buttonY = ScreenHelper.getButtonY(colorPicker);
        int buttonHeight = ScreenHelper.getButtonHeight();
        int buttonWidth = ScreenHelper.getButtonWidth(colorPicker);
        int buttonGap = ScreenHelper.getButtonGap();

        int copyX = ColorPickerInteraction.getColorPickerHSVX(colorPicker);
        int pasteX = copyX + buttonWidth + buttonGap;
        int resetX = pasteX + buttonWidth + buttonGap;

        return ScreenHelper.isHovered(mouseX, mouseY, copyX, buttonY, buttonWidth, buttonHeight) ||
                ScreenHelper.isHovered(mouseX, mouseY, pasteX, buttonY, buttonWidth, buttonHeight) ||
                ScreenHelper.isHovered(mouseX, mouseY, resetX, buttonY, buttonWidth, buttonHeight);
    }

    public int getColorPickerButtonY(OpenColorPicker picker) {
        return ColorPickerInteraction.getColorPickerAlphaY(picker) + (PADDING / 2 * 3);
    }

    public int getColorPickerButtonHeight() {
        return 16;
    }

    public int getColorPickerButtonGap() {
        return 4;
    }

    public int getColorPickerButtonWidth(OpenColorPicker picker) {
        return (ColorPickerInteraction.getColorPickerHSVWidth(picker) - getColorPickerButtonGap() * 2) / 3;
    }

    private boolean isOverOpenColorPickerControl(int mouseX, int mouseY) {
        if (activeColorPicker == null) {
            return false;
        }

        int x = ColorPickerInteraction.getColorPickerHSVX(activeColorPicker);
        int width = ColorPickerInteraction.getColorPickerHSVWidth(activeColorPicker);

        int barHeight = ColorPickerInteraction.getColorPickerBarHeight();

        // HSV square
        if (ScreenHelper.isHovered(
            mouseX,
            mouseY,
            x,
            ColorPickerInteraction.getColorPickerHSVY(activeColorPicker),
            width,
            ColorPickerInteraction.getColorPickerHSVHeight())
        ) {
            return true;
        }

        // Hue bar
        if (ScreenHelper.isHovered(
            mouseX, mouseY, x, ColorPickerInteraction.getColorPickerHueY(activeColorPicker), width, barHeight)
        ) {
            return true;
        }

        // Alpha bar
        //noinspection RedundantIfStatement
        if (ScreenHelper.isHovered(
            mouseX, mouseY, x, ColorPickerInteraction.getColorPickerAlphaY(activeColorPicker), width, barHeight)
        ) {
            return true;
        }

        return false;
    }

    private boolean isOverConfigInteraction(int mouseX, int mouseY) {
        for (ConfigInteraction interaction : configTypeInteractions) {
            if (ScreenHelper.isHovered(
                mouseX,
                mouseY,
                interaction.x(),
                interaction.y(),
                interaction.width(),
                interaction.height()
            )) {
                return true;
            }
        }

        return false;
    }

    private boolean isOverScrollbar(int mouseX, int mouseY) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());

        int scrollbarX = x + getMenuWidth() - PADDING - SCROLLBAR_WIDTH;

        int visibleHeight =
            AriesScreenLayout.getContentVisibleHeight(this.height, getMenuHeight(), PADDING, this.font.lineHeight);

        int scrollbarHeight = AriesScreenLayout.calculateScrollbarHeight(visibleHeight, PADDING);

        int thumbHeight = AriesScreenLayout.calculateThumbHeight(scrollbarHeight, contentHeight, visibleHeight);

        int thumbY =
            AriesScreenLayout.calculateThumbY(
                AriesScreenLayout.getScrollbarTop(
                    this.height, getMenuHeight(), PADDING, this.font.lineHeight
                ),
                scrollbarHeight,
                thumbHeight,
                scrollOffset,
                contentHeight,
                visibleHeight
            );

        return ScreenHelper.isHovered(mouseX, mouseY, scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight);
    }

    boolean isAuthorHovered(int mouseX, int mouseY) {
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

    public boolean isOverColorPicker(int mouseX, int mouseY) {
        OpenColorPicker colorPicker = getActiveColorPicker();

        if (colorPicker == null) {
            return false;
        }

        int boxX = colorPicker.getX() - colorPicker.getWidth() / 2;
        int boxY = colorPicker.getY();

        return ScreenHelper.isHovered(
            mouseX,
            mouseY,
            boxX,
            boxY,
            colorPicker.getWidth(),
            colorPicker.getHeight()
        );
    }

    // responsive menu layout dimensions based on current screen resolution
    int getCategoryWidth() {
        return (int) (getMenuWidth() / 3.5);
    }

    int getMenuWidth() {
        return (int) (MENU_WIDTH * getScale());
    }

    int getMenuHeight() {
        return (int) (MENU_HEIGHT * getScale());
    }

    public int getScreenWidth() {
        return width;
    }

    public int getScreenHeight() {
        return height;
    }

    private float getScale() {
        return Mth.clamp(Math.min(this.width / 1920f, this.height / 1080f), 1.0f, 2.0f);
    }

    public AriesCategory getSelectedCategory() {
        return selectedCategory;
    }

    public List<ConfigInteraction> getConfigTypeInteractions() {
        return configTypeInteractions;
    }

    // scrolling
    public int getScrollOffset() {
        return scrollOffset;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public void setSelectedCategory(AriesCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    // editing states
    public EditState getEditingState() {
        return editingState;
    }

    public void setEditingState(EditState state) {
        editingState = state;
    }

    // keybinds
    public KeybindConfig getListeningKeybind() {
        return listeningKeybind;
    }

    public void setListeningKeybind(KeybindConfig keybind) {
        listeningKeybind = keybind;
    }

    // sliders
    public ConfigInteraction getActiveSlider() {
        return activeSlider;
    }

    public void setActiveSlider(ConfigInteraction slider) {
        activeSlider = slider;
    }

    // color picker
    public OpenColorPicker getActiveColorPicker() {
        return activeColorPicker;
    }

    public void closeColorPicker() {
        activeColorPicker = null;
    }

    // menu saving
    @Override
    public void removed() {
        AriesScreenCache.savedScrollPosition = scrollOffset;
        AriesScreenCache.category = selectedCategory;

        super.removed();
    }

    // keyboard handlers
    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (keyboardHandling.charTyped(event)) {
            return true;
        }

        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (keyboardHandling.keyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    // mouse handlers
    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean handled) {
        if (mouseHandling.mouseClicked(event)) {
            return true;
        }

        return super.mouseClicked(event, handled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (mouseHandling.mouseDragged(event)) {
            return true;
        }

        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (mouseHandling.mouseReleased()) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseHandling.mouseScrolled(mouseX, mouseY, scrollY)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}

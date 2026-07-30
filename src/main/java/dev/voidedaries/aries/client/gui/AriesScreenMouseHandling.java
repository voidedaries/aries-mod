package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.ColorConfig;
import dev.voidedaries.aries.client.feature.types.interaction.*;
import dev.voidedaries.aries.client.render.feature.ConfigInteraction;
import dev.voidedaries.aries.client.render.feature.ConfigTypeRenderer;
import dev.voidedaries.aries.client.render.feature.OpenColorPicker;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class AriesScreenMouseHandling {
    private final AriesScreen screen;

    private boolean draggingScrollbar;
    private int scrollbarOffset;

    private ColorPickerDrag activeColorPickerDrag;

    private enum ColorPickerDrag {
        HSV_AREA,
        HUE_BAR,
        ALPHA_BAR
    }

    public AriesScreenMouseHandling(AriesScreen screen) {
        this.screen = screen;
    }

    public boolean mouseClicked(MouseButtonEvent event) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();

        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT && event.button() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            return false;
        }

        SocialButton social = screen.getHoveredSocial(mouseX, mouseY);

        if (social != null) {
            Util.getPlatform().openUri(social.url());
        }

        // cancel text editing when clicking elsewhere
        if (screen.getEditingState() != null) {
            screen.setEditingState(null);
        }

        // close color picker when clicking outside
        if (screen.getActiveColorPicker() != null && !screen.isOverColorPicker(mouseX, mouseY)) {
            screen.closeColorPicker();
            activeColorPickerDrag = null;
        }

        if (screen.getActiveColorPicker() != null) {
            if (handleColorPickerButtons(mouseX, mouseY)) {
                return true;
            }
        }

        // color picker HSV area
        if (screen.getActiveColorPicker() != null) {
            OpenColorPicker colorPicker = screen.getActiveColorPicker();
            ColorPickerState state = colorPicker.getState();

            int hsvBoxX = ColorPickerInteraction.getColorPickerHSVX(colorPicker);
            int hsvBoxY = ColorPickerInteraction.getColorPickerHSVY(colorPicker);
            int hsvBoxWidth = ColorPickerInteraction.getColorPickerHSVWidth(colorPicker);
            int hsvBoxHeight = ColorPickerInteraction.getColorPickerHSVHeight();

            if (ScreenHelper.isHovered(mouseX, mouseY, hsvBoxX, hsvBoxY, hsvBoxWidth, hsvBoxHeight)) {
                activeColorPickerDrag = ColorPickerDrag.HSV_AREA;

                ColorPickerInteraction.updateHSVArea(state, mouseX, mouseY, hsvBoxX, hsvBoxY, hsvBoxWidth, hsvBoxHeight);

                return true;
            }
        }

        // hue & alpha bars
        if (screen.getActiveColorPicker() != null) {
            OpenColorPicker colorPicker = screen.getActiveColorPicker();
            ColorPickerState state = colorPicker.getState();

            int barX = ColorPickerInteraction.getColorPickerHSVX(colorPicker);
            int barWidth = ColorPickerInteraction.getColorPickerHSVWidth(colorPicker);

            int hueY = ColorPickerInteraction.getColorPickerHueY(colorPicker);
            int alphaY = ColorPickerInteraction.getColorPickerAlphaY(colorPicker);

            int barHeight = (int) ((double) AriesScreen.PADDING / 2 * 1.75);

            if (ScreenHelper.isHovered(mouseX, mouseY, barX, hueY, barWidth, barHeight)) {
                activeColorPickerDrag = ColorPickerDrag.HUE_BAR;

                ColorPickerInteraction.updateHue(
                    state,
                    mouseX,
                    barX,
                    barWidth
                );

                return true;
            }


            if (ScreenHelper.isHovered(mouseX, mouseY, barX, alphaY, barWidth, barHeight)) {
                activeColorPickerDrag = ColorPickerDrag.ALPHA_BAR;

                ColorPickerInteraction.updateAlpha(
                    state,
                    mouseX,
                    barX,
                    barWidth
                );

                return true;
            }
        }

        // scrollbar
        int x = ScreenHelper.centreX(screen.getScreenWidth(), screen.getMenuWidth());
        int y = ScreenHelper.centreY(screen.getScreenHeight(), screen.getMenuHeight());

        int scrollbarX = x + screen.getMenuWidth() - AriesScreen.PADDING - AriesScreen.SCROLLBAR_WIDTH;

        int visibleHeight =
            AriesScreenLayout.getContentVisibleHeight(
                screen.getScreenHeight(), screen.getMenuHeight(), AriesScreen.PADDING, screen.getFont().lineHeight
            );

        int scrollbarHeight = AriesScreenLayout.calculateScrollbarHeight(visibleHeight, AriesScreen.PADDING);

        int thumbHeight = AriesScreenLayout.calculateThumbHeight(
            scrollbarHeight,
            screen.getContentHeight(),
            visibleHeight
        );

        int thumbY = AriesScreenLayout.calculateThumbY(
            AriesScreenLayout.getScrollbarTop(
                screen.getScreenHeight(),
                screen.getMenuHeight(),
                AriesScreen.PADDING,
                screen.getFont().lineHeight
            ),
            scrollbarHeight,
            thumbHeight,
            screen.getScrollOffset(),
            screen.getContentHeight(),
            visibleHeight
        );

        int scrollbarHitboxWidth = 12;

        if (ScreenHelper.isHovered(mouseX, mouseY,
            scrollbarX - (scrollbarHitboxWidth - AriesScreen.SCROLLBAR_WIDTH) / 2, thumbY,
            scrollbarHitboxWidth,
            thumbHeight)) {

            draggingScrollbar = true;
            scrollbarOffset = mouseY - thumbY;

            screen.closeColorPicker();
            activeColorPickerDrag = null;
            screen.setActiveSlider(null);
            screen.setEditingState(null);

            return true;
        }

        // search bar
        if (screen.isOverSearchBar(mouseX, mouseY)) {
            screen.getSearchBar().setFocused(true);
            return true;
        }

        if (!screen.isOverSearchBar(mouseX, mouseY)) {
            screen.getSearchBar().setFocused(false);
        }

        // categories
        int startY = (int) (y + AriesScreen.PADDING + screen.getFont().lineHeight + AriesScreen.PADDING * 1.5);

        int index = 0;

        for (AriesCategory category : AriesCategory.values()) {

            int entryY = startY + index * (screen.getFont().lineHeight + AriesScreen.PADDING);

            if (ScreenHelper.isHovered(
                mouseX, mouseY, x, entryY, screen.getCategoryWidth(), screen.getFont().lineHeight)
            ) {
                if (screen.getSelectedCategory() != category) {
                    screen.setSelectedCategory(category);
                    AriesScreenCache.category = category;

                    screen.setScrollOffset(0);
                    AriesScreenCache.savedScrollPosition = 0;
                }

                return true;
            }

            index++;
        }

        if (screen.getActiveColorPicker() != null) {
            ColorPickerState state = screen.getActiveColorPicker().getState();

            int padding = AriesScreen.PADDING / 2;

            int hsvBoxX = screen.getActiveColorPicker().getX() + padding;
            int hsvBoxY = screen.getActiveColorPicker().getY();
            int hsvBoxWidth = screen.getActiveColorPicker().getWidth() - padding * 2;
            int hsvBoxHeight = (int) (AriesScreen.COLOR_PICKER_BOX_HEIGHT / 2.75);


            if (ScreenHelper.isHovered(mouseX, mouseY, hsvBoxX, hsvBoxY, hsvBoxWidth, hsvBoxHeight)) {
                activeColorPickerDrag = ColorPickerDrag.HSV_AREA;

                ColorPickerInteraction.updateHSVArea(
                    state,
                    mouseX, mouseY,
                    hsvBoxX, hsvBoxY,
                    hsvBoxWidth, hsvBoxHeight
                );

                return true;
            }
        }

        if (screen.getActiveColorPicker() == null) {
            for (ConfigInteraction interaction : screen.getConfigTypeInteractions()) {

                if (!ScreenHelper.isHovered(
                    mouseX, mouseY,
                    interaction.x(), interaction.y(),
                    interaction.width(), interaction.height()
                )) {
                    continue;
                }

                if (interaction.click() != null) {
                    interaction.click().accept(event);
                    return true;
                }

                if (interaction.config() instanceof SliderValue slider) {
                    if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        screen.setEditingState(new SliderEditState(slider));
                        return true;
                    }

                    if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        screen.setActiveSlider(interaction);
                        ConfigTypeRenderer.updateSlider(mouseX, interaction);
                        return true;
                    }
                }

                if (interaction.config() instanceof ColorConfig color) {
                    if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        screen.setEditingState(new ColorEditState(color));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean mouseDragged(MouseButtonEvent event) {

        // scrollbar
        if (draggingScrollbar) {
            int visibleHeight =
                AriesScreenLayout.getContentVisibleHeight(
                    screen.height,
                    screen.getMenuHeight(),
                    AriesScreen.PADDING,
                    screen.getFont().lineHeight
                );

            int scrollbarHeight = AriesScreenLayout.calculateScrollbarHeight(
                visibleHeight,
                AriesScreen.PADDING
            );

            int thumbHeight = AriesScreenLayout.calculateThumbHeight(
                scrollbarHeight,
                screen.getContentHeight(),
                visibleHeight
            );

            int scrollOffset = AriesScreenLayout.calculateScrollOffsetFromDrag(
                event.y(),
                scrollbarOffset,
                AriesScreenLayout.getScrollbarTop(
                    screen.height,
                    screen.getMenuHeight(),
                    AriesScreen.PADDING,
                    screen.getFont().lineHeight
                ),
                scrollbarHeight,
                thumbHeight,
                screen.getContentHeight(),
                visibleHeight
            );

            scrollOffset = AriesScreenLayout.clampScroll(
                scrollOffset,
                screen.getContentHeight(),
                visibleHeight
            );

            screen.setScrollOffset(scrollOffset);

            AriesScreenCache.savedScrollPosition = scrollOffset;

            return true;
        }

        // color dragging
        if (activeColorPickerDrag != null && screen.getActiveColorPicker() != null) {
            OpenColorPicker colorPicker = screen.getActiveColorPicker();

            ColorPickerState state = colorPicker.getState();

            int barX = ColorPickerInteraction.getColorPickerHSVX(colorPicker);
            int barWidth = ColorPickerInteraction.getColorPickerHSVWidth(colorPicker);

            switch (activeColorPickerDrag) {

                case HSV_AREA -> ColorPickerInteraction.updateHSVArea(
                    state,
                    (int) event.x(),
                    (int) event.y(),
                    barX,
                    ColorPickerInteraction.getColorPickerHSVY(colorPicker),
                    barWidth,
                    ColorPickerInteraction.getColorPickerHSVHeight()
                );

                case HUE_BAR -> ColorPickerInteraction.updateHue(
                    state,
                    (int) event.x(),
                    barX,
                    barWidth
                );

                case ALPHA_BAR -> ColorPickerInteraction.updateAlpha(
                    state,
                    (int) event.x(),
                    barX,
                    barWidth
                );
            }

            return true;
        }

        // slider updater
        if (screen.getActiveSlider() != null) {
            ConfigTypeRenderer.updateSlider(
                (int) event.x(),
                screen.getActiveSlider()
            );

            return true;
        }


        return false;
    }

    public boolean mouseReleased() {
        boolean handled = false;

        if (screen.getActiveSlider() != null) {
            screen.setActiveSlider(null);
            handled = true;
        }

        if (draggingScrollbar) {
            draggingScrollbar = false;
            handled = true;
        }

        OpenColorPicker colorPicker = screen.getActiveColorPicker();

        if (activeColorPickerDrag != null && colorPicker != null) {
            colorPicker.getConfig().set(colorPicker.getState().getARGB());
            handled = true;
        }

        activeColorPickerDrag = null;

        if (handled) {
            AriesConfig.save();
        }

        return handled;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {

        if (screen.getActiveColorPicker() != null) {
            return true;
        }

        int x = ScreenHelper.centreX(screen.getScreenWidth(), screen.getMenuWidth());
        int y = ScreenHelper.centreY(screen.getScreenHeight(), screen.getMenuHeight());

        int visibleHeight =
            AriesScreenLayout.getContentVisibleHeight(
                screen.getScreenHeight(),
                screen.getMenuHeight(),
                AriesScreen.PADDING,
                screen.getFont().lineHeight
            );

        int scrollbarTop =
            AriesScreenLayout.getScrollbarTop(
                screen.getScreenHeight(),
                screen.getMenuHeight(),
                AriesScreen.PADDING,
                screen.getFont().lineHeight
            );

        boolean menuBounds = ScreenHelper.isHovered(
            (int) mouseX,
            (int) mouseY,
            x + screen.getCategoryWidth(),
            scrollbarTop,
            screen.getMenuWidth() - screen.getCategoryWidth() - AriesScreen.PADDING,
            screen.getMenuHeight() - (scrollbarTop - y)
        );

        if (!menuBounds) {
            return false;
        }

        int scrollOffset = screen.getScrollOffset();

        scrollOffset -= (int) (scrollY * 15);

        scrollOffset = AriesScreenLayout.clampScroll(
            scrollOffset,
            screen.getContentHeight(),
            visibleHeight
        );

        screen.setScrollOffset(scrollOffset);
        AriesScreenCache.savedScrollPosition = scrollOffset;

        return true;
    }

    private boolean handleColorPickerButtons(int mouseX, int mouseY) {
        OpenColorPicker colorPicker = screen.getActiveColorPicker();

        int buttonY = screen.getColorPickerButtonY(colorPicker);
        int buttonHeight = screen.getColorPickerButtonHeight();
        int buttonWidth = screen.getColorPickerButtonWidth(colorPicker);
        int buttonGap = screen.getColorPickerButtonGap();

        int copyX = ColorPickerInteraction.getColorPickerHSVX(colorPicker);
        int pasteX = copyX + buttonWidth + buttonGap;
        int resetX = pasteX + buttonWidth + buttonGap;

        // copy
        if (ScreenHelper.isHovered(
            mouseX,
            mouseY,
            copyX,
            buttonY,
            buttonWidth,
            buttonHeight
        )) {
            ColorClipboard.copy(colorPicker.getState().getARGB());
            return true;
        }

        // paste
        if (ScreenHelper.isHovered(
            mouseX,
            mouseY,
            pasteX,
            buttonY,
            buttonWidth,
            buttonHeight
        )) {
            if (ColorClipboard.hasColor()) {
                int color = ColorClipboard.paste(colorPicker.getConfig().get());

                colorPicker.getState().setARGB(color);
                colorPicker.getConfig().set(color);
            }

            return true;
        }

        // reset
        if (ScreenHelper.isHovered(
            mouseX,
            mouseY,
            resetX,
            buttonY,
            buttonWidth,
            buttonHeight
        )) {
            if (colorPicker.getConfig() instanceof ColorConfig colorConfig) {
                colorConfig.reset();
                colorPicker.getState().setARGB(colorConfig.get());
            }
        }

        return false;
    }

}

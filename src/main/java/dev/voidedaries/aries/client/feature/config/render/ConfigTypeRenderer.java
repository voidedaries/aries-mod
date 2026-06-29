package dev.voidedaries.aries.client.feature.config.render;

import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.config.types.ColorConfig;
import dev.voidedaries.aries.client.feature.config.types.IntConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ConfigTypeRenderer {
    private static final int PADDING = 10;

    private static final int TOGGLE_WIDTH = 25;
    public static final int TOGGLE_HEIGHT = TOGGLE_WIDTH / 2;
    private static final int TOGGLE_PADDING = 1;

    public static final int COLOR_PICKER_WIDTH = 25;
    public static final int COLOR_PICKER_HEIGHT = (int) (COLOR_PICKER_WIDTH / 1.5);

    private static final int SLIDER_WIDTH = 60;
    public static final int SLIDER_HEIGHT = 8;
    private static final int SLIDER_KNOB_SIZE = 10;

    public static ConfigInteraction drawToggle(
        GuiGraphics graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY
    ) {
        int toggleHeight = TOGGLE_HEIGHT;
        int toggleWidth = TOGGLE_WIDTH;
        int toggleX = controlRightX - TOGGLE_WIDTH;

        int entryHeight = font.lineHeight * 2 + PADDING;
        int toggleY = entryY + (entryHeight - toggleHeight) / 2;

        boolean value = (Boolean) config.get();

        // background
        graphics.fill(
            toggleX, toggleY, toggleX + toggleWidth, toggleY + toggleHeight,
            value ? 0xFF0058E1 : 0xFF2D3642
        );

        // knob
        int knobSize = toggleHeight - TOGGLE_PADDING * 2;
        int knobX = value
            ? toggleX + toggleWidth - knobSize - TOGGLE_PADDING
            : toggleX + TOGGLE_PADDING;

        graphics.fill(
            knobX,
            toggleY + TOGGLE_PADDING,
            knobX + knobSize,
            toggleY + toggleHeight - TOGGLE_PADDING,
            0xFF222933
        );

        return new ConfigInteraction(config, toggleX, toggleY, toggleWidth, toggleHeight);
    }

    public static ConfigInteraction drawSlider(
        GuiGraphics graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY
    ) {
        IntConfig slider = (IntConfig) config;

        int width = SLIDER_WIDTH;
        int height = SLIDER_HEIGHT;

        int x = controlRightX - width;
        int y = entryY + font.lineHeight;

        int min = slider.getMin();
        int max = slider.getMax();
        int value = slider.get();

        float percent = (value - min) / (float)(max - min);
        percent = Math.clamp(percent, 0f, 1f);

        int fillWidth = (int)(width * percent);

        // background
        graphics.fill(x, y, x + width, y + height, 0xFF2D3642);

        // filled track
        graphics.fill(x, y, x + fillWidth, y + height, 0xFF0058E1);

        // knob
        int knobSize = SLIDER_KNOB_SIZE;

        int knobX = (int) (x + percent * (width - knobSize));
        int knobY = y + (height / 2) - (knobSize / 2);

        graphics.fill(
            knobX,
            knobY,
            knobX + knobSize,
            knobY + knobSize,
            0xFFFFFFFF
        );

        return new ConfigInteraction(config, x, y, width, knobSize);
    }

    public static ConfigInteraction drawColorPicker(
        GuiGraphics graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY
    ) {
        ColorConfig color = (ColorConfig) config;

        int colorPickerWidth = COLOR_PICKER_WIDTH;
        int colorPickerHeight = COLOR_PICKER_HEIGHT;

        int value = color.get();

        int x = controlRightX - colorPickerWidth;
        int y = entryY + font.lineHeight;

        graphics.fill(x, y, x + colorPickerWidth, y + colorPickerHeight, value);

        return new ConfigInteraction(config, x, y, colorPickerWidth, colorPickerHeight);
    }

}

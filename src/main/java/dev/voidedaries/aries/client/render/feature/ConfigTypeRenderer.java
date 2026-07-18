package dev.voidedaries.aries.client.render.feature;

import com.mojang.blaze3d.platform.InputConstants;
import dev.voidedaries.aries.client.feature.types.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ConfigTypeRenderer {
    private static final int PADDING = 10;

    private static final int TOGGLE_WIDTH = 25;
    public static final int TOGGLE_HEIGHT = TOGGLE_WIDTH / 2;
    private static final int TOGGLE_PADDING = 1;

    private static final int KEYBIND_WIDTH = 30;
    public static final int KEYBIND_HEIGHT = TOGGLE_WIDTH / 2;

    public static final int COLOR_PICKER_WIDTH = 25;
    public static final int COLOR_PICKER_HEIGHT = (int) (COLOR_PICKER_WIDTH / 1.5);

    private static final int SLIDER_WIDTH = 50;
    public static final int SLIDER_HEIGHT = 8;
    private static final int SLIDER_KNOB_HEIGHT = 12;
    private static final int SLIDER_KNOB_PADDING = 2;


    public static int getConfigHeight(AriesConfigType<?> config) {
        return switch (config.getType()) {
            case TOGGLE -> TOGGLE_HEIGHT;
            case SLIDER -> SLIDER_HEIGHT;
            case COLOR -> COLOR_PICKER_HEIGHT;
            case KEYBIND -> KEYBIND_HEIGHT;

            default -> 0;
        };
    }

    private static int getSliderMaxTextWidth(Font font, SliderValue slider, SliderEditState editingSlider) {
        float min = slider.getMin();
        float max = slider.getMax();

        int width = 0;

        width = Math.max(width, font.width(formatSliderValue(min)));
        width = Math.max(width, font.width(formatSliderValue(max)));

        if (min < 0) {
            width = Math.max(width, font.width(formatSliderValue(Math.abs(min))));
        }

        if (max < 0) {
            width = Math.max(width, font.width(formatSliderValue(Math.abs(max))));
        }

        width = Math.max(width, font.width(formatSliderValue(max + 0.5f)));

        if (editingSlider != null && editingSlider.getSlider() == slider) {
            width = Math.max(width, font.width(formatSliderValue(max) + "|"));
        }

        return width;
    }

    private static String formatSliderValue(float value) {
        if (value == Math.floor(value)) {
            return String.valueOf((int) value);
        }

        return String.format("%.2f", value)
            .replaceAll("0+$", "")
            .replaceAll("\\.$", "");
    }

    public static ConfigInteraction drawToggle(
        GuiGraphicsExtractor graphics,
        Font ignoredfont,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY
    ) {
        int toggleHeight = TOGGLE_HEIGHT;
        int toggleWidth = TOGGLE_WIDTH;
        int toggleX = controlRightX - TOGGLE_WIDTH;

        boolean value = (Boolean) config.get();

        // background
        graphics.fill(
            toggleX, entryY, toggleX + toggleWidth, entryY + toggleHeight,
            value ? 0xFF0058E1 : 0xFF2D3642
        );

        // knob
        int knobSize = toggleHeight - TOGGLE_PADDING * 2;
        int knobX = value
            ? toggleX + toggleWidth - knobSize - TOGGLE_PADDING
            : toggleX + TOGGLE_PADDING;

        graphics.fill(
            knobX,
            entryY + TOGGLE_PADDING,
            knobX + knobSize,
            entryY + toggleHeight - TOGGLE_PADDING,
            0xFF222933
        );

        return new ConfigInteraction(config, toggleX, entryY, toggleWidth, toggleHeight);
    }

    public static ConfigInteraction drawSlider(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        SliderEditState editingSlider
    ) {
        SliderValue slider = (SliderValue) config;

        int width = SLIDER_WIDTH;
        int height = SLIDER_HEIGHT;

        int x = controlRightX - width;

        float min = slider.getMin();
        float max = slider.getMax();
        float value = slider.getAsFloat();

        float percent = (value - min) / (max - min);
        percent = Math.clamp(percent, 0f, 1f);

        int fillWidth = (int)(width * percent);

        // background
        graphics.fill(x, entryY, x + width, entryY + height, 0xFF2D3642);

        // filled track
        graphics.fill(x, entryY, x + fillWidth, entryY + height, 0xFF0058E1);

        String valueText;
        String displayText;

        if (editingSlider != null && editingSlider.getSlider() == slider) {
            valueText = editingSlider.getInput();

            displayText = valueText;

            if (editingSlider.showCaret()) {
                displayText += "|";
            }
        } else {
            valueText = formatSliderValue(value);
            displayText = valueText;
        }

        int maxTextWidth = getSliderMaxTextWidth(font, slider, editingSlider);

        int knobWidth = maxTextWidth + (SLIDER_KNOB_PADDING * 2);
        int knobHeight = SLIDER_KNOB_HEIGHT;

        int knobX = (int) (x + percent * (width - knobWidth));
        int knobY = entryY + (height / 2) - (knobHeight / 2);

        int textWidth = font.width(displayText);

        int textX = knobX + (knobWidth - textWidth) / 2;
        int textY = knobY + (knobHeight - font.lineHeight) / 2;
        textY++;

        // knob border
        graphics.fill(
            knobX - 1,
            knobY - 1,
            knobX + knobWidth + 1,
            knobY + knobHeight + 1,
            0xFF434E5B
        );

        // knob
        graphics.fill(
            knobX,
            knobY,
            knobX + knobWidth,
            knobY + knobHeight,
            0xFF151A21
        );

        //text
        graphics.text(font, Component.literal(displayText), textX, textY, 0xFFADB5C9);

        return new ConfigInteraction(config, x, entryY, width, height);
    }

    public static ConfigInteraction drawColorPicker(
        GuiGraphicsExtractor graphics,
        Font ignoredfont,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY
    ) {
        ColorConfig color = (ColorConfig) config;

        int colorPickerWidth = COLOR_PICKER_WIDTH;
        int colorPickerHeight = COLOR_PICKER_HEIGHT;

        int value = color.get();

        int x = controlRightX - colorPickerWidth;

        graphics.fill(x, entryY, x + colorPickerWidth, entryY + colorPickerHeight, value);

        return new ConfigInteraction(config, x, entryY, colorPickerWidth, colorPickerHeight);
    }

    public static ConfigInteraction drawKeybind(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        boolean listening
    ) {
        int keybindHeight = KEYBIND_HEIGHT;

        KeybindConfig keybind = (KeybindConfig) config;

        Component text;

        if (listening) {
            text = Component.translatable("key.aries.listening");
        } else if (keybind.getCurrentKey().getValue() == InputConstants.UNKNOWN.getValue()) {
            text = Component.translatable("key.aries.not_bound");
        } else {
            text = Component.literal(keybind.getCurrentKey().getDisplayName().getString());
        }

        int textWidth = font.width(text);

        int keybindWidth = Math.max(KEYBIND_WIDTH, textWidth + PADDING * 2);

        int keybindX = controlRightX - keybindWidth;

        // border
        graphics.fill(
            keybindX - 1,
            entryY - 1,
            keybindX + keybindWidth + 1,
            entryY + keybindHeight + 1,
            0xFF434E5B
        );

        // background
        graphics.fill(
            keybindX, entryY, keybindX + keybindWidth, entryY + keybindHeight, 0xFF151A21
        );

        int textX = keybindX + (keybindWidth - textWidth) / 2;
        int textY = entryY + (keybindHeight - font.lineHeight) / 2;

        textY++;

        graphics.text(font, text, textX, textY, 0xFFADB5C9);

        return new ConfigInteraction(config, keybindX, entryY, keybindWidth, keybindHeight);
    }

}

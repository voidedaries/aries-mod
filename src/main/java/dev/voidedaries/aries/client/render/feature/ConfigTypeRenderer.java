package dev.voidedaries.aries.client.render.feature;

import com.mojang.blaze3d.platform.InputConstants;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.types.*;
import dev.voidedaries.aries.client.feature.types.interaction.*;
import dev.voidedaries.aries.client.gui.AriesScreen;
import dev.voidedaries.aries.client.gui.ScreenHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigTypeRenderer {
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

    private static final int LIST_WIDTH = 30;
    public static final int LIST_HEIGHT = LIST_WIDTH / 2;

    private static final int BUTTON_WIDTH = 30;
    public static final int BUTTON_HEIGHT = BUTTON_WIDTH / 2;
    public static final int CONTROL_PADDING = AriesScreen.PADDING / 4;


    public static int getConfigHeight(AriesConfigType<?> config) {
        return switch (config.getType()) {
            case TOGGLE -> TOGGLE_HEIGHT;
            case SLIDER -> SLIDER_HEIGHT;
            case COLOR -> COLOR_PICKER_HEIGHT;
            case KEYBIND -> KEYBIND_HEIGHT;
            case LIST -> LIST_HEIGHT;
            case BUTTON -> BUTTON_HEIGHT;

            default -> 0;
        };
    }

    public static int getConfigWidth(Font font, AriesConfigType<?> config) {
        return switch (config.getType()) {
            case TOGGLE -> TOGGLE_WIDTH;

            case SLIDER -> getSliderWidth();

            case COLOR -> getColorPickerWidth(font, (ColorConfig) config);

            case KEYBIND -> getKeybindWidth(font, (KeybindConfig) config);

            case LIST -> getListWidth(font, (ListConfig<?>) config);

            case BUTTON -> getButtonWidth(font, (ButtonConfig) config);

            default -> 0;
        };
    }

    private static int getSliderWidth() {
        return SLIDER_WIDTH + CONTROL_PADDING;
    }

    private static int getButtonWidth(Font font, ButtonConfig button) {
        int textWidth = font.width(button.getLabel());

        return Math.max(BUTTON_WIDTH, textWidth + AriesScreen.PADDING) + CONTROL_PADDING;
    }

    private static int getListWidth(Font font, ListConfig<?> list) {
        Object value = list.get();

        Component valueComponent =
            Component.literal(String.valueOf(value));

        int textWidth = font.width(valueComponent);

        int width = Math.max(
            LIST_WIDTH,
            textWidth + AriesScreen.PADDING
        );

        return width + CONTROL_PADDING;
    }

    private static int getKeybindWidth(Font font, KeybindConfig keybind) {
        int normalWidth = font.width(
            keybind.getCurrentKey().getValue() ==
                InputConstants.UNKNOWN.getValue()
                ? Component.translatable("key.aries.not_bound")
                : Component.literal(keybind.getCurrentKey().getDisplayName().getString())
        );

        int listeningWidth = font.width(Component.translatable("key.aries.listening"));
        int textWidth = Math.max(normalWidth, listeningWidth);
        int width = Math.max(KEYBIND_WIDTH, textWidth + AriesScreen.PADDING);

        return width + CONTROL_PADDING;
    }

    private static int getColorPickerWidth(Font font, ColorConfig color) {
        int value = color.get();

        String hex = ColorConfig.formatColor(value);

        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        float scale = 0.85f;

        int padding = AriesScreen.PADDING / 2;

        int textWidth = font.width(hex);
        int scaledTextWidth = (int) (textWidth * scale);

        int boxWidth = scaledTextWidth + (padding * 2) + CONTROL_PADDING;

        int gap = AriesScreen.PADDING / 2;

        return boxWidth + gap + COLOR_PICKER_WIDTH + CONTROL_PADDING;
    }

    public static List<ConfigInteraction> draw(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        int mouseX, int mouseY,

        EditState editingState,
        OpenColorPicker activeColorPicker,
        KeybindConfig listeningKeybind,

        Consumer<ColorEditState> onColorEdit,
        Consumer<OpenColorPicker> onColorPicker,
        Consumer<OpenListPicker> onListPicker,
        Consumer<KeybindConfig> onKeybind
    ) {
        return switch (config.getType()) {
            case TEXT -> List.of();

            case TOGGLE -> List.of(drawToggle(graphics, font, config, controlRightX, entryY));

            case BUTTON -> List.of(
                drawButton(
                    graphics,
                    font,
                    config,
                    controlRightX,
                    entryY,
                    mouseX,
                    mouseY
                )
            );

            case SLIDER -> List.of(
                drawSlider(graphics,
                    font,
                    config,
                    controlRightX,
                    entryY,
                    editingState instanceof SliderEditState sliderEdit ? sliderEdit : null
                )
            );

            case COLOR -> drawColorPicker(
                graphics,
                font,
                config,
                controlRightX,
                entryY,
                mouseX, mouseY,
                editingState instanceof ColorEditState colorEdit ? colorEdit : null,
                activeColorPicker != null && activeColorPicker.getConfig() == config ? activeColorPicker.getState() : null,
                onColorEdit,
                onColorPicker
            );

            case LIST -> List.of(drawList(graphics, font, config, controlRightX, entryY, mouseX, mouseY, onListPicker));

            case KEYBIND -> List.of(
                drawKeybind(
                    graphics,
                    font,
                    config,
                    controlRightX,
                    entryY,
                     listeningKeybind == config,
                    onKeybind
                )
            );
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

    public static ConfigInteraction drawButton(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        int mouseX,
        int mouseY
    ) {
        ButtonConfig button = (ButtonConfig) config;

        Component text = button.getLabel();

        int padding = AriesScreen.PADDING;
        int textWidth = font.width(text);

        int width = Math.max(BUTTON_WIDTH, textWidth + padding);
        int height = BUTTON_HEIGHT;

        int textY = entryY + (height - font.lineHeight) / 2;
        textY++;
        int boxX = controlRightX - width;

        boolean hovered = ScreenHelper.isHovered(mouseX, mouseY, boxX, entryY, width, height);

        graphics.fill(boxX - 1, entryY - 1, boxX + width + 1, entryY + height + 1, 0xFF434E5B);

        graphics.fill(boxX, entryY, boxX + width, entryY + height, hovered ? 0xFF222933 : 0xFF151A21);

        graphics.text(
            font,
            text,
            boxX + (width - font.width(text)) / 2,
            textY,
            0xFFADB5C9
        );

        return new ConfigInteraction(config, boxX, entryY, width, height, _ -> button.press());
    }

    public static ConfigInteraction drawList(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        int mouseX,
        int mouseY,
        Consumer<OpenListPicker> onOpen
    ) {
        ListConfig<?> list = (ListConfig<?>) config;

        int width = LIST_WIDTH;
        int height = LIST_HEIGHT;

        Object value = list.get();

        Component valueComponent = Component.literal(String.valueOf(value));

        int textWidth = font.width(valueComponent);
        int boxWidth = Math.max(width, textWidth + AriesScreen.PADDING);
        int boxX = controlRightX - boxWidth;

        boolean hovered = ScreenHelper.isHovered(mouseX, mouseY, boxX, entryY, boxWidth, height);

        // border
        graphics.fill(boxX - 1, entryY - 1, boxX + boxWidth + 1, entryY + height + 1, 0xFF434E5B);

        // background
        graphics.fill(boxX, entryY, boxX + boxWidth, entryY + height, hovered ? 0xFF222933 : 0xFF151A21);

        // text
        graphics.text(
            font,
            valueComponent,
            boxX + (boxWidth - textWidth) / 2,
            entryY + (height - font.lineHeight) / 2,
            0xFFADB5C9
        );

        return new ConfigInteraction(config, boxX, entryY, boxWidth, height, _ -> onOpen.accept(
            new OpenListPicker(list, boxX, entryY, boxWidth, height)
        ));
    }

    public static ConfigInteraction drawToggle(
        GuiGraphicsExtractor graphics,
        Font ignoredfont,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY
    ) {
        int toggleWidth = TOGGLE_WIDTH;
        int toggleHeight = TOGGLE_HEIGHT;
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

        return new ConfigInteraction(config, toggleX, entryY, toggleWidth, toggleHeight, _ -> {
            BooleanConfig bool = (BooleanConfig) config;
            bool.set(!bool.get());
            AriesConfig.save();
        });
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

    public static void updateSlider(int mouseX, ConfigInteraction interaction) {
        if (!(interaction.config() instanceof SliderValue slider)) {
            return;
        }

        int x = interaction.x();
        int width = interaction.width();

        float percent = (mouseX - x) / (float) width;
        percent = Mth.clamp(percent, 0f, 1f);

        slider.setFromPercent(percent);
    }

    public static List<ConfigInteraction> drawColorPicker(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        int mouseX,
        int mouseY,
        ColorEditState editingColor,
        ColorPickerState pickerState,
        Consumer<ColorEditState> onEdit,
        Consumer<OpenColorPicker> onColorPicker
    ) {
        ColorConfig color = (ColorConfig) config;

        List<ConfigInteraction> interactions = new ArrayList<>();

        int colorPickerWidth = COLOR_PICKER_WIDTH;
        int colorPickerHeight = COLOR_PICKER_HEIGHT;

        int value = pickerState != null ? pickerState.getARGB() : color.get();

        int x = controlRightX - colorPickerWidth;

        String hex = ColorConfig.formatColor(value);

        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        float scale = 0.85f;

        int padding = AriesScreen.PADDING / 2;

        int textWidth = font.width(hex);
        int textHeight = font.lineHeight;

        int scaledTextWidth = (int) (textWidth * scale);
        int scaledTextHeight = (int) (textHeight * scale);

        int boxWidth = scaledTextWidth + (padding * 2) + CONTROL_PADDING;
        int boxHeight = scaledTextHeight + (padding * 2);

        int boxX = x - boxWidth - (AriesScreen.PADDING / 2);

        int pickerCenterY = entryY + (colorPickerHeight / 2);
        int boxY = pickerCenterY - (boxHeight / 2);

        // outer border
        graphics.fill(boxX - 1, boxY - 1, boxX + boxWidth + 1, boxY + boxHeight + 1, 0xFF434E5B);

        //text border
        graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF151A21);

        graphics.pose().pushMatrix();

        graphics.pose().translate(boxX + padding, boxY + padding);
        graphics.pose().scale(scale, scale);

        boolean hexHovered = ScreenHelper.isHovered(mouseX, mouseY, boxX, boxY, boxWidth, boxHeight);
        boolean editing = editingColor != null && editingColor.getColor() == color;
        boolean active = hexHovered || editing;

        int alphaColor = active ? 0xFFFFFFFF : 0xFFADB5C9;
        int redColor = active ? 0xFFFF5555 : 0xFFADB5C9;
        int greenColor = active ? 0xFF55FF55 : 0xFFADB5C9;
        int blueColor = active ? 0xFF5555FF : 0xFFADB5C9;

        //what texts to show
        String cleanHex = editing ? editingColor.getInput() : hex;

        MutableComponent hexComponent = Component.empty();

        // Add the actual characters
        if (!cleanHex.isEmpty()) {
            hexComponent.append(
                Component.literal(cleanHex.substring(0, Math.min(2, cleanHex.length())))
                    .withColor(alphaColor)
            );
        }

        if (cleanHex.length() > 2) {
            hexComponent.append(
                Component.literal(cleanHex.substring(2, Math.min(4, cleanHex.length())))
                    .withColor(redColor)
            );
        }

        if (cleanHex.length() > 4) {
            hexComponent.append(
                Component.literal(cleanHex.substring(4, Math.min(6, cleanHex.length())))
                    .withColor(greenColor)
            );
        }

        if (cleanHex.length() > 6) {
            hexComponent.append(
                Component.literal(cleanHex.substring(6, Math.min(8, cleanHex.length())))
                    .withColor(blueColor)
            );
        }


        //add caret separately
        if (editing && editingColor.showCaret()) {
            hexComponent.append(
                Component.literal("|")
                    .withColor(0xFFADB5C9)
            );
        }

        //text
        graphics.text(font, hexComponent, 0, 0, 0xFFADB5C9);

        graphics.pose().popMatrix();

        //color picker
        graphics.fill(x, entryY, x + colorPickerWidth, entryY + colorPickerHeight, value);

        interactions.add(
            new ConfigInteraction(
                config,
                boxX,
                boxY,
                boxWidth,
                boxHeight,
                _ -> onEdit.accept(new ColorEditState(color))
            )
        );

        interactions.add(
            new ConfigInteraction(
                config,
                x,
                entryY,
                colorPickerWidth,
                colorPickerHeight,
                _ -> onColorPicker.accept(
                    new OpenColorPicker(
                        color,
                        x + colorPickerWidth / 2,
                        entryY + colorPickerHeight,
                        AriesScreen.COLOR_PICKER_BOX_WIDTH,
                        AriesScreen.COLOR_PICKER_BOX_HEIGHT
                    )
                )
            )
        );

        return interactions;
    }

    public static ConfigInteraction drawKeybind(
        GuiGraphicsExtractor graphics,
        Font font,
        AriesConfigType<?> config,
        int controlRightX,
        int entryY,
        boolean listening,
        Consumer<KeybindConfig> onListen
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

        int keybindWidth = Math.max(KEYBIND_WIDTH, textWidth + AriesScreen.PADDING);

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

        return new ConfigInteraction(
            config, keybindX, entryY, keybindWidth, keybindHeight, _ -> onListen.accept(keybind)
        );
    }

}

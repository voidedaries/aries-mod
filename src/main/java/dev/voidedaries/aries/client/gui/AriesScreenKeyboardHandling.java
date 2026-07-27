package dev.voidedaries.aries.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.types.interaction.SliderValue;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

public class AriesScreenKeyboardHandling {
    private final AriesScreen screen;

    public AriesScreenKeyboardHandling(AriesScreen screen) {
        this.screen = screen;
    }

    public boolean charTyped(@NonNull CharacterEvent event) {
        if (screen.getEditingState() != null) {
            screen.getEditingState().addChar((char) event.codepoint());
            return true;
        }

        return false;
    }

    public boolean keyPressed(KeyEvent event) {

        // keyboard handling
        if (screen.getListeningKeybind() != null) {

            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                screen.getListeningKeybind().set(InputConstants.UNKNOWN.getValue());
                AriesConfig.save();

                screen.setListeningKeybind(null);
                return true;
            }

            screen.getListeningKeybind().set(event.key());
            AriesConfig.save();

            screen.setListeningKeybind(null);
            return true;
        }

        // text editing
        if (screen.getEditingState() != null) {
            switch (event.key()) {
                case GLFW.GLFW_KEY_ESCAPE -> {
                    screen.setEditingState(null);
                    return true;
                }

                case GLFW.GLFW_KEY_ENTER -> {
                    if (screen.getEditingState().apply()) {
                        AriesConfig.save();
                    }

                    screen.setEditingState(null);
                    return true;
                }

                case GLFW.GLFW_KEY_BACKSPACE -> {
                    screen.getEditingState().backspace();
                    return true;
                }
            }

            return true;
        }

        if (screen.getActiveColorPicker() != null && event.key() == GLFW.GLFW_KEY_ESCAPE) {
            screen.closeColorPicker();
            return true;
        }

        if (screen.getActiveSlider() != null && screen.getActiveSlider().config() instanceof SliderValue slider) {
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

        return false;
    }

}

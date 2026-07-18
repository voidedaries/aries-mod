package dev.voidedaries.aries.client.feature.types;

/**
 * Defines the supported configuration types used by Aries.
 *
 * <p>Configuration types are used by the menu renderer {@link dev.voidedaries.aries.client.gui.AriesScreen} to determine
 * which UI component should be displayed for each {@link dev.voidedaries.aries.client.feature.types.AriesConfigType}.</p>
 */
public enum ConfigTypes {
    LIST,
    TOGGLE,
    SLIDER,
    BUTTON,
    KEYBIND,
    COLOR,
    TEXT
}

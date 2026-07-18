package dev.voidedaries.aries.client.feature.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;
import dev.voidedaries.aries.mixin.KeyMappingMixin;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class KeybindConfig extends AriesConfigType<Integer> {
    private KeyMapping keyMapping;
    public static final KeyMapping.Category ARIES_CATEGORY =
        KeyMapping.Category.register(Aries.id("main_category"));

    public KeybindConfig(String key, Integer defaultKey) {
        super(key, defaultKey);
    }

    public void register() {
        keyMapping = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                "key.aries." + getKey(),
                InputConstants.Type.KEYSYM,
                get(),
                ARIES_CATEGORY
            )
        );

        syncKeyMapping();
    }

    public InputConstants.Key getCurrentKey() {
        if (keyMapping != null) {
            return ((KeyMappingMixin) keyMapping).aries$getBoundKey();
        }

        return InputConstants.Type.KEYSYM.getOrCreate(get());
    }

    @Override
    public void set(Integer value) {
        if (get().equals(value)) {
            syncKeyMapping();
            return;
        }

        setValue(value);
        syncKeyMapping();
    }

    private void syncKeyMapping() {
        if (keyMapping != null) {
            keyMapping.setKey(
                get() == InputConstants.UNKNOWN.getValue()
                    ? InputConstants.UNKNOWN
                    : InputConstants.Type.KEYSYM.getOrCreate(get())
            );

            KeyMapping.resetMapping();
        }
    }

    public boolean isDown() {
        return keyMapping != null && keyMapping.isDown();
    }

    @Override
    public ConfigTypes getType() {
        return ConfigTypes.KEYBIND;
    }
}

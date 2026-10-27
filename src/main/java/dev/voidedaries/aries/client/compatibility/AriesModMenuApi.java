package dev.voidedaries.aries.client.compatibility;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.voidedaries.aries.client.gui.AriesScreen;

public class AriesModMenuApi implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new AriesScreen(parent, null);
    }
}

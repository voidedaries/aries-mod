package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.client.feature.types.AriesCategory;

public final class AriesScreenCache {
    public static AriesCategory category = AriesCategory.ABOUT; // first category on first opening
    public static int savedScrollPosition = 0;
    public static String savedSearchText = "";

    private AriesScreenCache() {}
}

package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.entry.FeatureEntry;

import java.util.Locale;

public class SearchHelper {

    private void ScreenHelper() {}

    public static boolean matches(AriesFeature feature, String query) {
        if (query.isBlank()) {
            return true;
        }

        String search = query.toLowerCase(Locale.ROOT);

        if (feature.getName().getString().toLowerCase(Locale.ROOT).contains(search)) {
            return true;
        }

        if (feature.getDescription().getString().toLowerCase(Locale.ROOT).contains(search)) {
            return true;
        }

        for (FeatureEntry entry : feature.getEntries()) {
            if (entry.name().getString().toLowerCase(Locale.ROOT).contains(search)) {
                return true;
            }

            if (entry.description().getString().toLowerCase(Locale.ROOT).contains(search)) {
                return true;
            }
        }

        return false;
    }

}

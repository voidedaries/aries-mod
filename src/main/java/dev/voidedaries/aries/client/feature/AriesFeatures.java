package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.feature.config.AriesConfigType;

import java.util.ArrayList;
import java.util.List;

public class AriesFeatures {

    private static final List<AriesFeature> FEATURES = new ArrayList<>();

    public static final CompactChatFeature COMPACT_CHAT = register(new CompactChatFeature());
    public static final CompactChatTimeFeature COMPACT_CHAT_TIME = register(new CompactChatTimeFeature());

    private static <T extends AriesFeature> T register(T feature) {
        FEATURES.add(feature);
        return feature;
    }

    public static List<AriesFeature> getFeatures() {
        return FEATURES;
    }

    public static List<AriesConfigType<?>> getAllConfigs() {
        List<AriesConfigType<?>> list = new ArrayList<>();

        for (AriesFeature feature : FEATURES) {
            list.addAll(feature.getConfigs());
        }

        return list;
    }

    public static void init() {}

}

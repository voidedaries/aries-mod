package dev.voidedaries.aries.client.feature;

import dev.voidedaries.aries.client.feature.config.types.AriesConfigType;

import java.util.ArrayList;
import java.util.List;

public class AriesFeatures {

    private static final List<AriesFeature> FEATURES = new ArrayList<>();

    public static final CompactChatFeature COMPACT_CHAT = register(new CompactChatFeature());
    public static final CompactChatTimeFeature COMPACT_CHAT_TIME = register(new CompactChatTimeFeature());

    public static final EtherwarpOutlineFeature ETHERWARP_OUTLINE = register(new EtherwarpOutlineFeature());
    public static final EtherwarpOutlineWidthFeature ETHERWARP_OUTLINE_WIDTH = register(new EtherwarpOutlineWidthFeature());
    public static final EtherwarpOutlineValidFeature ETHERWARP_OUTLINE_VALID = register(new EtherwarpOutlineValidFeature());
    public static final EtherwarpOutlineInvalidFeature ETHERWARP_OUTLINE_INVALID = register(new EtherwarpOutlineInvalidFeature());

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

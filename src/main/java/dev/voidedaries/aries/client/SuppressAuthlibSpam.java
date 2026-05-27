package dev.voidedaries.aries.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.filter.RegexFilter;

import org.apache.logging.log4j.core.Logger;

public final class SuppressAuthlibSpam {

    private SuppressAuthlibSpam() {}

    public static void init() throws IllegalAccessException {
        Logger logger = (Logger) LogManager.getLogger("com.mojang.authlib");

        Filter filter = RegexFilter.createFilter(
            ".*Failed to verify signature.*|.*textures property.*",
            null,
            false,
            Filter.Result.DENY,
            Filter.Result.NEUTRAL
        );

        logger.addFilter(filter);
    }

}

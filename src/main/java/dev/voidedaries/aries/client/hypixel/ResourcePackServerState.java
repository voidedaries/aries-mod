package dev.voidedaries.aries.client.hypixel;

import java.util.Locale;

public final class ResourcePackServerState {
    private static volatile boolean hypixel;

    private ResourcePackServerState() {}

    public static void setServerAddress(String address) {
        if (address == null) {
            hypixel = false;
            return;
        }

        String host = address.trim().toLowerCase(Locale.ROOT);

        int colon = host.lastIndexOf(':');
        if (colon >= 0 && host.indexOf(':') == colon) {
            host = host.substring(0, colon);
        }

        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }

        hypixel = host.equals("hypixel.net") || host.endsWith(".hypixel.net");
    }

    public static boolean isHypixel() {
        return hypixel;
    }

    public static void clear() {
        hypixel = false;
    }
}
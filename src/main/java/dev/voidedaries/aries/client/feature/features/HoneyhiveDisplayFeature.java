package dev.voidedaries.aries.client.feature.features;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import dev.voidedaries.aries.client.gui.location.HudBounds;
import dev.voidedaries.aries.client.gui.location.HudPosition;
import dev.voidedaries.aries.client.gui.location.HudRenderable;
import dev.voidedaries.aries.client.message.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.prefs.Preferences;

public class HoneyhiveDisplayFeature extends AriesFeature implements HudRenderable {
    public final BooleanConfig enabled = addConfig(new BooleanConfig("honeyhive_display.enabled", false));
    private final HudPosition hudPosition = new HudPosition();

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(HoneyhiveDisplayFeature.class);
    private static final String TIMER_KEY = "honeyhive_end_time";
    private static final long HONEYHIVE_DURATION = 60 * 60 * 1000L;
    private static long honeyHiveEndTime = PREFERENCES.getLong(TIMER_KEY, 0L);

    public HoneyhiveDisplayFeature() {
        super(
            Component.translatable("gui.category.torrhus_canyon.honeyhive_display.name"),
            Component.translatable("gui.category.torrhus_canyon.honeyhive_display.description"),
            AriesCategory.TORRHUS_CANYON
        );
    }

    public static void startTimer() {
        honeyHiveEndTime = System.currentTimeMillis() + HONEYHIVE_DURATION;
        PREFERENCES.putLong(TIMER_KEY, honeyHiveEndTime);
    }

    public static void resetTimer() {
        honeyHiveEndTime = 0L;
        PREFERENCES.remove(TIMER_KEY);
    }

    public static long getRemainingTime() {
        return Math.max(0L, honeyHiveEndTime - System.currentTimeMillis());
    }

    private String getFormattedTime() {
        long remainingTime = getRemainingTime();

        if (remainingTime <= 0L) {
            return "Ready";
        }

        long remainingSeconds = (getRemainingTime() + 999) / 1000;
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String getHudId() {
        return "honeyhive_display";
    }

    @Override
    public boolean isHudEnabled() {
        return enabled.get();
    }

    @Override
    public void setHudEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    @Override
    public HudPosition getHudPosition() {
        return hudPosition;
    }

    @Override
    public HudBounds renderHud(GuiGraphicsExtractor graphics, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();

        if (ChatMessage.contains("HIVE! You found")) {
            startTimer();
        }

        boolean ready = getRemainingTime() <= 0L;
        Component text = Component.literal(ready ? "Honeyhives: " : "Honeyhives in: ")
            .withStyle(style -> style.withColor(0xFFFFA500));

        Component timer = Component.literal(getFormattedTime())
            .withStyle(style -> style.withColor(ready ? 0xFF55FF55 : 0xFFFFA500));

        graphics.text(minecraft.font, text, x, y, 0xFFFFA500);
        graphics.text(minecraft.font, timer, x + minecraft.font.width(text), y, 0xFFFFA500);

        HudBounds bounds = getHudBounds();

        return new HudBounds(x, y, bounds.width(), bounds.height());
    }

    @Override
    public HudBounds getHudBounds() {
        Minecraft minecraft = Minecraft.getInstance();

        boolean ready = getRemainingTime() <= 0L;

        Component text = Component.literal((ready ? "Honeyhives " : "Honeyhives in: ") + getFormattedTime());

        return new HudBounds(0, 0, minecraft.font.width(text), minecraft.font.lineHeight);
    }
}

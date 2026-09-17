package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.Aries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class AriesScreenChangelog {

    private static final int PADDING = 10;
    private static final int CHANGELOG_INDENT = PADDING;

    private record ChangelogLine(String text, ChangelogLineType type) {}

    private enum ChangelogLineType {
        TITLE,
        HEADING,
        BULLET,
        TEXT,
        SPACER
    }

    private static List<ChangelogLine> changelogLines = List.of();

    private AriesScreenChangelog() {}

    public static void load() {
        List<ChangelogLine> loadedLines = new ArrayList<>();
        Identifier id = Aries.id("changelog.md");

        try {
            Minecraft minecraft = Minecraft.getInstance();

            Resource resource = minecraft.getResourceManager()
                .getResource(id)
                .orElse(null);

            if (resource == null) {
                Aries.log("Could not find changelog resource: {}", id);
                return;
            }

            try (
                InputStream stream = resource.open();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
            ) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty()) {
                        loadedLines.add(new ChangelogLine("", ChangelogLineType.SPACER));
                    } else if (line.startsWith("# ")) {
                        loadedLines.add(new ChangelogLine(line.substring(2), ChangelogLineType.TITLE));
                    } else if (line.startsWith("### ")) {
                        loadedLines.add(new ChangelogLine(line.substring(4), ChangelogLineType.HEADING));
                    } else if (line.startsWith("- ")) {
                        loadedLines.add(new ChangelogLine(line.substring(2), ChangelogLineType.BULLET));
                    } else {
                        loadedLines.add(new ChangelogLine(line, ChangelogLineType.TEXT));
                    }
                }
            }

        } catch (IOException e) {
            Aries.log("Failed to load changelog: {}", e);
        }

        changelogLines = loadedLines;
    }

    public static int draw(
        GuiGraphicsExtractor graphics,
        Font font,
        int x,
        int y,
        int categoryWidth,
        int menuWidth,
        int menuHeight,
        int scrollOffset
    ) {
        int contentX = x + categoryWidth + PADDING * 2;
        int contentY = (int) (y + PADDING + font.lineHeight + PADDING * 1.5);

        int contentLeft = x + categoryWidth + PADDING;
        int contentTop = contentY - PADDING;
        int contentRight = x + menuWidth - PADDING;
        int contentBottom = y + menuHeight;

        int contentWidth = contentRight - contentX;

        graphics.enableScissor(contentLeft, contentTop, contentRight, contentBottom);

        int currentY = contentY - scrollOffset;

        for (ChangelogLine line : changelogLines) {
            int lineHeight = 0;

            switch (line.type()) {
                case TITLE -> {
                    graphics.text(font, line.text(), contentX, currentY, 0xFF0058E1);
                    lineHeight = PADDING;
                }

                case HEADING -> {
                    currentY += PADDING / 2;

                    graphics.text(font, line.text(), contentX + (CHANGELOG_INDENT / 2), currentY, 0xFF0058E1);
                    lineHeight = PADDING;
                }

                case BULLET -> {
                    List<FormattedCharSequence> wrapped =
                        font.split(
                            Component.literal(line.text()), (int) (contentWidth - CHANGELOG_INDENT * 1.5 - PADDING)
                        );

                    float scale = 0.9f;

                    graphics.pose().pushMatrix();
                    graphics.pose().translate((float) (contentX + CHANGELOG_INDENT * 1.5), currentY);

                    graphics.text(font, "•", 0, 0, 0xFFDDDDDD);

                    graphics.pose().popMatrix();

                    for (FormattedCharSequence text : wrapped) {
                        graphics.pose().pushMatrix();
                        graphics.pose().translate((float) (contentX + CHANGELOG_INDENT * 1.5 + PADDING), currentY);
                        graphics.pose().scale(scale, scale);

                        graphics.text(font, text, 0, 0, 0xFFDDDDDD);

                        graphics.pose().popMatrix();

                        currentY += (int) (PADDING * 1.25f);
                    }

                }

                case TEXT -> {
                    graphics.text(font, line.text(), contentX, currentY, 0xFFDDDDDD);

                    lineHeight = PADDING * 2;
                }

                case SPACER -> lineHeight = PADDING / 2;
            }

            currentY += lineHeight;
        }

        graphics.disableScissor();

        return currentY - contentY + font.lineHeight + PADDING / 2 + scrollOffset;
    }

}

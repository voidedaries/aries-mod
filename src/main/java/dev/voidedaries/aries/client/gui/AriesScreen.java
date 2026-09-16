package dev.voidedaries.aries.client.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.entry.FeatureEntry;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.feature.types.KeybindConfig;
import dev.voidedaries.aries.client.feature.types.ListConfig;
import dev.voidedaries.aries.client.feature.types.interaction.*;
import dev.voidedaries.aries.client.render.feature.ConfigInteraction;
import dev.voidedaries.aries.client.render.feature.ConfigTypeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AriesScreen extends Screen {
    private AriesCategory selectedCategory = AriesCategory.ABOUT; // first category on first opening

    public final List<SocialButton> socials = List.of(
        new SocialButton(Aries.id("socials/modrinth_logo.png"), ModConstants.MODRINTH_URL, "Modrinth"),
        new SocialButton(Aries.id("socials/github_logo.png"), ModConstants.GITHUB_URL, "Github"),
        new SocialButton(Aries.id("socials/discord_logo.png"), ModConstants.DISCORD_URL, "Discord")
    );

    private final SearchBar searchBar = new SearchBar();
    private boolean categoryManuallySelected = false;

    // mouse & keyboard handlers
    private AriesScreenKeyboardHandling keyboardHandling;
    private AriesScreenMouseHandling mouseHandling;

    //interaction
    private ConfigInteraction activeSlider = null;
    public KeybindConfig listeningKeybind;

    //config states
    private EditState editingState;
    private OpenColorPicker activeColorPicker;

    private OpenListPicker activeListPicker;

    // menu
    static final int MENU_WIDTH = 360;
    static final int MENU_HEIGHT = 220;
    public static final int PADDING = 10;
    private static final int CHANGELOG_INDENT = PADDING;

    private static final float DESCRIPTION_SCALE = 0.8f;

    private static final int FEATURE_SPACING = PADDING / 2;
    private static final int ENTRY_SPACING = PADDING / 2;

    // content
    private int contentHeight;

    // scrollbar
    static final int SCROLLBAR_WIDTH = 4;
    private int scrollOffset = 0;

    public static int COLOR_PICKER_BOX_WIDTH = 120;
    public static int COLOR_PICKER_BOX_HEIGHT = 110;

    private final List<ConfigInteraction> configTypeInteractions = new ArrayList<>();

    private record ChangelogLine(String text, ChangelogLineType type) {}
    private enum ChangelogLineType {
        TITLE,
        HEADING,
        BULLET,
        TEXT,
        SPACER
    }
    private List<ChangelogLine> changelogLines = List.of();

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
        //setters for menu
        configTypeInteractions.clear();

        keyboardHandling = new AriesScreenKeyboardHandling(this);
        mouseHandling = new AriesScreenMouseHandling(this);

        selectedCategory = AriesScreenCache.category;
        scrollOffset = AriesScreenCache.savedScrollPosition;

        searchBar.setText(AriesScreenCache.savedSearchText);

        changelogLines = loadChangelog();

        if (searchBar.hasQuery()) {
            updateSearchCategory();
        }

        if (AriesConfig.isNewVersion()) {
            selectedCategory = AriesCategory.CHANGELOG;
            categoryManuallySelected = true;
            scrollOffset = 0;
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        configTypeInteractions.clear();

        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        // drawing menu & categories
        graphics.fill(x, y, x + getMenuWidth(), y + getMenuHeight(), 0xFF222933);
        graphics.fill(x, y, x + getCategoryWidth(), y + getMenuHeight(), 0xFF151A21);

        drawCategories(graphics, mouseX, mouseY, delta);
        drawMenu(graphics, mouseX, mouseY, delta);

        int menuX = ScreenHelper.centreX(this.width, getMenuWidth());
        int menuY = ScreenHelper.centreY(this.height, getMenuHeight());

        drawSearchBar(graphics, menuX, menuY);

        // update cursor
        updateCursor(graphics, mouseX, mouseY);
    }

    private void drawMenu(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        AriesCategory currentCategory = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        if (searchBar.hasQuery() && !categoryManuallySelected) {
            currentCategory = getSearchCategory();
        }

        // starting position for the main content area
        int contentX = x + getCategoryWidth() + PADDING * 2;

        int configRenderX = x + getMenuWidth() - PADDING * 2;

        // starting position below the menu header/title
        int contentY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);
        int entryContentY = contentY;

        MutableComponent modName =
            Component.literal("")
                .append(Component.translatable("aries.mod_name")
                .append(Component.literal(" • ").withColor(0xFFADB5C9)
                    .withStyle(s -> s.withUnderlined(false)))
                .append(Component.literal(ModConstants.displayVersion)));

        graphics.text(
            this.font,
            modName,
            x + getCategoryWidth() + 2 * PADDING,
            y + PADDING,
            0xFF0058E1
        );

        graphics.horizontalLine(
            x + getCategoryWidth() + PADDING,
            x + getMenuWidth() - PADDING,
            y + PADDING + this.font.lineHeight + PADDING / 2,
            0xFF2D3642
        );

        if (currentCategory == AriesCategory.ABOUT) {
            drawAboutCategory(graphics, x, y, mouseX, mouseY);
            return;
        }

        if (currentCategory == AriesCategory.CHANGELOG) {
            drawChangelogCategory(graphics, x, y);
            return;
        }

        if (currentCategory == AriesCategory.DEV) {
            Component warning = Component.translatable("gui.menu.category.dev.warning");

            float warningScale = 0.85f;

            List<FormattedCharSequence> warningLines =
                this.font.split(warning, (getMenuWidth() - getCategoryWidth()));

            int warningHeight = 0;

            for (FormattedCharSequence warningLine : warningLines) {
                graphics.pose().pushMatrix();

                graphics.pose().translate(contentX, entryContentY + warningHeight);
                graphics.pose().scale(warningScale, warningScale);

                graphics.text(this.font, warningLine, 0, 0, 0xFFFF5555);

                graphics.pose().popMatrix();

                warningHeight += (int) (this.font.lineHeight * warningScale);
            }

            entryContentY += warningHeight + PADDING;
        }

        // scissor bounds
        int contentLeft = x + getCategoryWidth() + PADDING;
        int contentTop = contentY - PADDING;
        int contentRight = x + getMenuWidth() - PADDING;
        int contentBottom = y + getMenuHeight();

        int visibleHeight = contentBottom - contentTop;

        //bounds for rendering features and configs
        graphics.enableScissor(
            contentLeft,
            contentTop,
            contentRight,
            contentBottom
        );

        // drawing features
        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            if (feature.getCategory() != currentCategory || !feature.isVisible()) {
                continue;
            }

            if (searchBar.hasQuery() && !SearchHelper.matches(feature, searchBar.getText())) {
                continue;
            }

            // translating content position into render position
            int renderY = entryContentY - scrollOffset;

            // feature title
            graphics.text(
                this.font,
                feature.getName(),
                contentX,
                renderY,
                0xFFFFFFFF
            );

            int featureConfigWidth = getConfigWidth(feature.getConfigs());

            int featureDescriptionRight = configRenderX- featureConfigWidth - PADDING;

            int featureDescriptionWidth = featureDescriptionRight - contentX;

            List<FormattedCharSequence> featureDescription =
                splitDescriptionWithScale(feature.getDescription(), featureDescriptionWidth);

            int featureDescriptionHeight =
                drawDescription(graphics, featureDescription, contentX, renderY + this.font.lineHeight + PADDING / 2);

            int featureTextHeight = this.font.lineHeight + featureDescriptionHeight;

            int featureConfigHeight = getConfigHeight(feature.getConfigs());

            int featureConfigY;

            if (featureConfigHeight <= featureTextHeight) {
                featureConfigY = renderY + (featureTextHeight - featureConfigHeight) / 2;
            } else {
                featureConfigY = renderY;
            }

            // feature config interactions
            for (AriesConfigType<?> config : feature.getConfigs()) {
                if (!config.isVisible()) {
                    continue;
                }

                List<ConfigInteraction> interactions =
                    drawConfigs(graphics, config,  configRenderX, featureConfigY, mouseX, mouseY);

                if (interactions != null) {
                    configTypeInteractions.addAll(interactions);

                    if (!interactions.isEmpty()) {
                        int maxHeight = interactions.stream().mapToInt(ConfigInteraction::height).max().orElse(0);
                        featureConfigY += maxHeight + PADDING * 2;
                    }
                }
            }

            // tracks the next available vertical position in the content layout
            int nextContentY = entryContentY;
            nextContentY += Math.max(featureTextHeight, featureConfigHeight) + FEATURE_SPACING * 4;

            // drawing entries for feature specific sub-configs
            for (FeatureEntry entry : feature.getEntries()) {
                if (!entry.isVisible()) {
                    continue;
                }

                int entryDrawY = nextContentY - scrollOffset;

                graphics.text(
                    this.font,
                    entry.name(),
                    contentX,
                    entryDrawY,
                    0xFFFFFFFF
                );

                int entryConfigWidth = getConfigWidth(entry.configs());

                int entryDescriptionRight = configRenderX - entryConfigWidth - PADDING;

                int entryDescriptionWidth = entryDescriptionRight - contentX;

                List<FormattedCharSequence> entryDescription =
                    splitDescriptionWithScale(entry.description(), entryDescriptionWidth);

                int descriptionHeight = drawDescription(
                    graphics,
                    entryDescription,
                    contentX,
                    entryDrawY + this.font.lineHeight + PADDING / 2
                );

                int textHeight = this.font.lineHeight + descriptionHeight;

                int configHeight = getConfigHeight(entry.configs());

                // calculate config render position based on available text height
                int configRenderY;

                if (configHeight <= textHeight) {
                    configRenderY = entryDrawY + (textHeight - configHeight) / 2;
                } else {
                    configRenderY = entryDrawY;
                }

                // entry config rendering
                for (AriesConfigType<?> config : entry.configs()) {
                    if (!config.isVisible()) {
                        continue;
                    }

                    List<ConfigInteraction> interactions =
                        drawConfigs(graphics, config,  configRenderX, configRenderY, mouseX, mouseY);

                    if (interactions != null) {
                        configTypeInteractions.addAll(interactions);

                        if (!interactions.isEmpty()) {
                            int maxHeight =
                                interactions.stream().mapToInt(ConfigInteraction::height).max().orElse(0);
                            configRenderY += maxHeight + PADDING * 2;
                        }
                    }
                }

                // move the layout cursor down by the entry's height
                nextContentY += Math.max(textHeight, configHeight) + ENTRY_SPACING * 4;
            }

            // position entries below the feature
            entryContentY = nextContentY + FEATURE_SPACING;
        }

        // calculate total scrollable content height
        contentHeight = entryContentY - contentY;
        scrollOffset = AriesScreenLayout.clampScroll(scrollOffset, contentHeight, visibleHeight);

        graphics.disableScissor();

        boolean needsScrollbar = contentHeight > visibleHeight;

        // scrollbar handling
        if (needsScrollbar) {
            drawScrollbar(
                graphics,
                contentRight - SCROLLBAR_WIDTH,
                contentTop + PADDING,
                (contentBottom - contentTop) - (PADDING * 2),
                visibleHeight
            );
        }

        // color picker handling
        if (activeColorPicker != null) {
            drawExpandedColorPicker(graphics, activeColorPicker, mouseX, mouseY);
        }

        // list picker handling
        if (activeListPicker != null) {
            drawExpandedListPicker(graphics, activeListPicker, mouseX, mouseY);
        }
    }

    private void drawChangelogCategory(
        GuiGraphicsExtractor graphics,
        int x,
        int y
    ) {
        int contentX = x + getCategoryWidth() + PADDING * 2;
        int contentY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        int contentLeft = x + getCategoryWidth() + PADDING;
        int contentTop = contentY - PADDING;
        int contentRight = x + getMenuWidth() - PADDING;
        int contentBottom = y + getMenuHeight();

        int contentWidth = contentRight - contentX;

        int visibleHeight = contentBottom - contentTop;

        graphics.enableScissor(contentLeft, contentTop, contentRight, contentBottom);

        int currentY = contentY - scrollOffset;

        for (ChangelogLine line : changelogLines) {
            int lineHeight;

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

                    lineHeight = 0;
                }

                case TEXT -> {
                    graphics.text(font, line.text(), contentX, currentY, 0xFFDDDDDD);

                    lineHeight = PADDING * 2;
                }

                case SPACER -> lineHeight = PADDING / 2;
                default -> lineHeight = PADDING;
            }

            currentY += lineHeight;
        }

        graphics.disableScissor();

        contentHeight = currentY - contentY + this.font.lineHeight + PADDING / 2 + scrollOffset;

        scrollOffset = AriesScreenLayout.clampScroll(
            scrollOffset,
            contentHeight,
            visibleHeight
        );

        boolean needsScrollbar = contentHeight > visibleHeight;

        if (needsScrollbar) {
            drawScrollbar(
                graphics,
                contentRight - SCROLLBAR_WIDTH,
                contentTop + PADDING,
                (contentBottom - contentTop) - PADDING * 2,
                visibleHeight
            );
        }
    }

    private List<ChangelogLine> loadChangelog() {
        List<ChangelogLine> lines = new ArrayList<>();

        Identifier id = Aries.id("changelog.md");

        try {
            Minecraft minecraft = Minecraft.getInstance();

            var resource = minecraft.getResourceManager()
                .getResource(id)
                .orElse(null);

            if (resource == null) {
                Aries.log("Could not find changelog resource: {}", id);
                return lines;
            }

            try (
                InputStream stream = resource.open();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
            ) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty()) {
                        lines.add(new ChangelogLine("", ChangelogLineType.SPACER));
                    } else if (line.startsWith("# ")) {
                        lines.add(new ChangelogLine(line.substring(2), ChangelogLineType.TITLE));
                    } else if (line.startsWith("### ")) {
                        lines.add(new ChangelogLine(line.substring(4), ChangelogLineType.HEADING));
                    } else if (line.startsWith("- ")) {
                        lines.add(new ChangelogLine(line.substring(2), ChangelogLineType.BULLET));
                    } else {
                        lines.add(new ChangelogLine(line, ChangelogLineType.TEXT));
                    }
                }
            }

        } catch (IOException e) {
            Aries.log("Failed to load changelog: {}", e);
        }

        return lines;
    }

    private void drawAboutCategory(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        int contentAreaWidth = getMenuWidth() - getCategoryWidth();

        int contentY = y + PADDING + this.font.lineHeight + PADDING / 2;

        int logoWidth = 48;
        int logoHeight = (int)(logoWidth * (592f / 720f));

        int logoX = x + getCategoryWidth() + (contentAreaWidth - logoWidth) / 2;

        // logo
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            ModConstants.ARIES_LOGO,
            logoX,
            contentY,
            0,
            0,
            logoWidth,
            logoHeight,
            720,
            592,
            720,
            592
        );

        int centerX = x + getCategoryWidth() + contentAreaWidth / 2;

        Component title = Component.translatable("aries.mod_name");

        graphics.text(
            this.font,
            title,
            centerX - this.font.width(title) / 2,
            contentY + logoHeight + PADDING / 2,
            0xFF0058E1
        );

        // overview
        Component overview = Component.translatable("aries.overview");

        graphics.pose().pushMatrix();

        float scale = 0.9f;

        graphics.pose().translate(centerX, (float) (contentY + logoHeight + PADDING * 2));
        graphics.pose().scale(scale, scale);

        graphics.text(
            this.font,
            overview,
            -this.font.width(overview) / 2,
            0,
            0xFFFFFFFF
        );

        graphics.pose().popMatrix();

        // desc
        List<FormattedCharSequence> description = this.font.split(
            Component.translatable("aries.description"),
            260
        );

        int descriptionY = (int) (contentY + logoHeight + PADDING * 3.5);

        graphics.pose().pushMatrix();

        graphics.pose().translate(centerX, descriptionY);
        graphics.pose().scale(scale, scale);

        int scaledY = 0;

        for (FormattedCharSequence line : description) {
            graphics.text(
                this.font,
                line,
                -this.font.width(line) / 2,
                scaledY,
                0xFFADB5C9
            );

            scaledY += this.font.lineHeight;
        }

        graphics.pose().popMatrix();

        int infoY = descriptionY + (description.size() * this.font.lineHeight) + PADDING / 2;

        graphics.horizontalLine(
            (x + getCategoryWidth() + PADDING),
            x + getMenuWidth() - (PADDING * 2),
            infoY - PADDING / 5,
            0xFF2D3642
        );

        if (ModConstants.version == null) {
            return;
        }

        if (ModConstants.minecraftVersion == null) {
            return;
        }

        List<Component> info = List.of(
            Component.translatable("aries.about.version", ModConstants.version),
            Component.translatable("aries.about.minecraft", ModConstants.minecraftVersion),
            Component.translatable("aries.about.loader"),
            Component.translatable("aries.about.author", "VoidedAries")
        );

        graphics.pose().pushMatrix();

        graphics.pose().translate(centerX, (float) ((float) infoY - PADDING * 1.5));
        graphics.pose().scale(scale, scale);

        for (Component line : info) {
            graphics.text(
                this.font,
                line,
                -this.font.width(line) / 2,
                scaledY,
                0xFFADB5C9
            );

            scaledY += this.font.lineHeight + 4;
        }

        graphics.pose().popMatrix();

        int socialSize = 16;
        int spacing = (int) (PADDING * 1.5);
        int iconTextSpacing = PADDING / 2;

        int totalWidth = 0;

        for (SocialButton social : socials) {
            Component name = Component.literal(social.name());

            totalWidth += socialSize
                + iconTextSpacing
                + this.font.width(name);

            totalWidth += spacing;
        }

        totalWidth -= spacing;

        int socialX = centerX - totalWidth / 2;
        int socialY = (int) (infoY + PADDING * 6.5);
        int socialTextOffset = 1;

        SocialButton hoveredSocial = getHoveredSocial(mouseX, mouseY);

        for (SocialButton social : socials) {

            drawSocialIcon(graphics, social.icon(), socialX, socialY);

            socialX += socialSize + iconTextSpacing;

            MutableComponent plainName = Component.literal(social.name());

            boolean hovered = social.equals(hoveredSocial);

            Component name = plainName.withStyle(style -> style.withUnderlined(hovered));

            graphics.text(
                this.font,
                name,
                socialX,
                socialY + (socialSize - this.font.lineHeight) / 2 + socialTextOffset,
                0xFFFFFFFF
            );

            socialX += this.font.width(plainName) + spacing;
        }
    }

    private void drawSocialIcon(
        GuiGraphicsExtractor graphics,
        Identifier texture,
        int x,
        int y
    ) {
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            x,
            y,
            0,
            0,
            16,
            16,
            512,
            512,
            512,
            512
        );
    }

    private void drawSearchBar(
        GuiGraphicsExtractor graphics,
        int menuX,
        int menuY
    ) {
        int height = 12;
        int maxWidth = 80;

        int x = (int) (menuX + getMenuWidth() - (PADDING * 1.5) - maxWidth);
        int y = (int) (menuY + PADDING + (this.font.lineHeight - height) / 1.75);

        int textOffset = 1;

        // background
        graphics.fill(
            x,
            y - 1,
            x + maxWidth,
            y + height,
            0xFF151A21
        );

        graphics.enableScissor(x + 2, y, x + maxWidth - 2, y + height);

        String text = searchBar.getText();

        int textWidth = this.font.width(text);
        int visibleWidth = maxWidth - PADDING;

        searchBar.updateScrollOffset(textWidth, visibleWidth);

        int textX = x + (PADDING / 2) - searchBar.getScrollOffset();
        int textY = y + (height - this.font.lineHeight) / 2 + textOffset;

        if (text.isEmpty() && !searchBar.isFocused()) {
            graphics.text(
                this.font,
                Component.translatable("gui.menu.searchbar"),
                textX,
                textY,
                0xFFADB5C9
            );
        } else if(!text.isEmpty()) {
            graphics.text(
                this.font,
                Component.literal(text),
                textX,
                textY,
                0xFFFFFFFF
            );
        }

        // cursor
        if (searchBar.isFocused() && searchBar.shouldShowCursor()) {
            int cursorX = textX + this.font.width(text);

            graphics.fill(
                cursorX,
                y + 1,
                cursorX + 1,
                y + height - 2,
                0xFFADB5C9
            );
        }

        graphics.disableScissor();
    }

    //method for drawing descriptions needed for features & entries (separate method for scaling purposes)
    private int drawDescription(GuiGraphicsExtractor graphics, List<FormattedCharSequence> lines, int x, int y) {
        float scale = DESCRIPTION_SCALE;

        // description text
        for (int line = 0; line < lines.size(); line++) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y + line * this.font.lineHeight);
            graphics.pose().scale(scale, scale);
            graphics.text(this.font, lines.get(line), 0, 0, 0xFFADB5C9);
            graphics.pose().popMatrix();
        }

        return (int) (lines.size() * this.font.lineHeight * scale);
    }

    private List<FormattedCharSequence> splitDescriptionWithScale(
        Component description, int availableWidth
    ) {
        return this.font.split(description, (int) (availableWidth / AriesScreen.DESCRIPTION_SCALE));
    }

    private void drawExpandedListPicker(GuiGraphicsExtractor graphics, OpenListPicker picker, int mouseX, int mouseY) {
        ListConfig<?> config = picker.getConfig();
        List<?> values = config.getValues();

        int optionHeight = (int) (PADDING * 1.5);

        int width = Math.max(
            picker.getWidth(),
            values.stream()
                .map(value -> font.width(Component.literal(String.valueOf(value))))
                .max(Integer::compareTo)
                .orElse(0) + PADDING
        );

        int height = values.size() * optionHeight;

        int x = picker.getX() + picker.getWidth() / 2 - width / 2;

        // Open upward if there isn't enough room below
        int y = picker.getY() + picker.getHeight();

        if (y + height > this.height - PADDING) {
            y = picker.getY() - height;
        }

        // outer border
        graphics.fill(
            x - 1,
            y - 1,
            x + width + 1,
            y + height + 1,
            0xFF434E5B
        );

        // background
        graphics.fill(
            x,
            y,
            x + width,
            y + height,
            0xFF151A21
        );

        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);

            int optionY = y + i * optionHeight;

            boolean hovered = ScreenHelper.isHovered(
                mouseX,
                mouseY,
                x,
                optionY,
                width,
                optionHeight
            );

            if (hovered) {
                graphics.fill(
                    x,
                    optionY,
                    x + width,
                    optionY + optionHeight,
                    0xFF222933
                );
            }

            Component text = Component.literal(String.valueOf(value));

            graphics.text(
                this.font,
                text,
                x + (width - this.font.width(text)) / 2,
                optionY + (optionHeight - this.font.lineHeight) / 2,
                0xFFADB5C9
            );
        }
    }

    private void drawExpandedColorPicker(
        GuiGraphicsExtractor graphics,
        OpenColorPicker picker,
        int mouseX,
        int mouseY
    ) {
        ColorPickerState state = picker.getState();
        int centerX = picker.getX();
        int topY = picker.getY();

        int width = picker.getWidth();
        int height = picker.getHeight();

        int padding = PADDING / 2;

        //triangle for box
        int triangleSize = 12;
        int triangleHeight = triangleSize / 2;

        int boxX = centerX - width / 2;
        int boxY = topY + triangleHeight;
        boxY--;

        int startY = boxY + (PADDING * 2);

        //hsv box
        int hsvBoxX = boxX + padding;
        int hsvBoxY = (int) (startY - (PADDING * 1.25));

        int hsvBoxWidth = width - (padding * 2);
        int hsvBoxHeight = (int) (COLOR_PICKER_BOX_HEIGHT / 2.75);

        int hueColor = Color.HSBtoRGB(state.getHue(), 1.0f, 1.0f);

        int triangleOffset = 2;
        int hueBarY = (int) (hsvBoxY + hsvBoxHeight + (PADDING / 1.5));
        int alphaBarY = hueBarY + (PADDING * 2);

        // Rectangle
        graphics.fill(boxX, boxY, boxX + width, boxY + height, 0xFF434E5B);
        graphics.fill(boxX + 1, boxY + 1, boxX + width - 1, boxY + height - 1, 0xFF222933);

        // Outer triangle
        ScreenHelper.drawTriangle(
            graphics, centerX, topY, triangleSize, 0xFF434E5B, ScreenHelper.TriangleDirection.UP
        );

        // Inner triangle
        ScreenHelper.drawTriangle(
            graphics, centerX, topY + 1, 10, 0xFF222933, ScreenHelper.TriangleDirection.UP
        );

        AriesScreenLayout.drawHSVBox(
            graphics, hsvBoxX, hsvBoxY, hsvBoxWidth, hsvBoxHeight, hueColor
        );

        // hsv indicator selector
        int selectorX = hsvBoxX + (int) (state.getSaturation() * hsvBoxWidth);
        int selectorY = hsvBoxY + (int) ((1 - state.getBrightness()) * hsvBoxHeight);

        graphics.fill(selectorX - 3, selectorY - 3, selectorX + 4, selectorY + 4, 0xFF000000);
        graphics.fill(selectorX - 2, selectorY - 2, selectorX + 3, selectorY + 3, 0xFFFFFFFF);

        // triangle bar indicators (for alpha and hue bars)
        int barTriangleSize = 8;
        int hueSelectorX = hsvBoxX + (int)(state.getHue() * hsvBoxWidth);

        // hue
        ScreenHelper.drawTriangle(
            graphics,
            hueSelectorX,
            hueBarY - triangleHeight + triangleOffset,
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.DOWN
        );

        ScreenHelper.drawTriangle(
            graphics,
            hueSelectorX,
            hueBarY + (int)(padding * 1.75),
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.UP
        );

        AriesScreenLayout.drawHueBar(graphics, hsvBoxX, hueBarY, hsvBoxWidth, (int) (padding * 1.75));

        // alpha
        int alphaSelectorX = hsvBoxX + (int)(state.getAlpha() * hsvBoxWidth);

        ScreenHelper.drawTriangle(
            graphics,
            alphaSelectorX,
            alphaBarY - triangleHeight + triangleOffset,
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.DOWN
        );

        ScreenHelper.drawTriangle(
            graphics,
            alphaSelectorX,
            alphaBarY + (padding * 2) - triangleOffset,
            barTriangleSize,
            0xFFFFFFFF,
            ScreenHelper.TriangleDirection.UP
        );

        AriesScreenLayout.drawAlphaBar(
            graphics, hsvBoxX, alphaBarY, hsvBoxWidth, (int) (padding * 1.75), 0xFFFFFFFF
        );

        int buttonY = (alphaBarY + (padding * 3));
        int buttonHeight = 16;

        int buttonGap = 4;
        int buttonWidth = (hsvBoxWidth - buttonGap * 2) / 3;

        int pasteX = hsvBoxX + buttonWidth + buttonGap;
        int resetX = pasteX + buttonWidth + buttonGap;

        boolean copyHovered = ScreenHelper.isOverButton(mouseX, mouseY, hsvBoxX, buttonY, buttonWidth, buttonHeight);
        ScreenHelper.drawButton(graphics, this.font, hsvBoxX, buttonY, buttonWidth, buttonHeight, Component.translatable("gui.menu.color_picker.copy"), copyHovered);

        boolean pasteHovered = ScreenHelper.isOverButton(mouseX, mouseY, pasteX, buttonY, buttonWidth, buttonHeight);
        ScreenHelper.drawButton(graphics, this.font, pasteX, buttonY, buttonWidth, buttonHeight, Component.translatable("gui.menu.color_picker.paste"), pasteHovered);

        boolean resetHovered = ScreenHelper.isOverButton(mouseX, mouseY, resetX, buttonY, buttonWidth, buttonHeight);
        ScreenHelper.drawButton(graphics, this.font, resetX, buttonY, buttonWidth, buttonHeight, Component.translatable("gui.menu.color_picker.reset"), resetHovered);
    }

    private void drawScrollbar(GuiGraphicsExtractor graphics, int x, int y, int height, int visibleHeight) {
        // track
        graphics.fill(x, y, x + SCROLLBAR_WIDTH, y + height, 0xFF151A21);

        int thumbHeight = AriesScreenLayout.calculateThumbHeight(height, contentHeight, visibleHeight);

        int thumbY =
            AriesScreenLayout.calculateThumbY(y, height, thumbHeight, scrollOffset, contentHeight, visibleHeight);

        // thumb
        graphics.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFF0058E1);
    }

    //draws configs
    private List<ConfigInteraction> drawConfigs(
        GuiGraphicsExtractor graphics,
        AriesConfigType<?> config,
        int controlRightX,
        int configY,
        int mouseX,
        int mouseY
    ) {
        return ConfigTypeRenderer.draw(
            graphics,
            this.font,
            config,
            controlRightX,
            configY,
            mouseX, mouseY,

            editingState,
            activeColorPicker,
            listeningKeybind,

            state -> editingState = state,
            picker -> activeColorPicker = picker,
            picker -> activeListPicker = picker,
            keybind -> listeningKeybind = keybind
        );
    }

    private int getConfigHeight(List<AriesConfigType<?>> configs) {
        int height = 0;

        for (AriesConfigType<?> config : configs) {
            if (!config.isVisible()) {
                continue;
            }

            height += ConfigTypeRenderer.getConfigHeight(config) + PADDING * 2;
        }

        if (height > 0) {
            height -= PADDING * 2;
        }

        return height;
    }

    private int getConfigWidth(List<AriesConfigType<?>> configs) {
        int width = 0;

        for (AriesConfigType<?> config : configs) {
            if (!config.isVisible()) {
                continue;
            }

            width = Math.max(width, ConfigTypeRenderer.getConfigWidth(this.font, config));
        }

        return width;
    }

    private void drawCategories(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int startCategoryHeight = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        graphics.centeredText(
            this.font,
            Component.translatable("gui.menu.categories"),
            x + (getCategoryWidth() / 2),
            y + PADDING,
            0xFF0058E1
        );

        graphics.horizontalLine(
            x + PADDING,
            x + getCategoryWidth() - PADDING,
            y + PADDING + this.font.lineHeight + PADDING / 2,
            0xFF2D3642
        );

        AriesCategory current = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        int index = 0;

        for (AriesCategory category : AriesCategory.values()) {
            if (category == AriesCategory.CHANGELOG) {
                continue;
            }

            int entryY = startCategoryHeight + index * (this.font.lineHeight + PADDING);
            boolean categoryHovered =
                ScreenHelper.isHovered(mouseX, mouseY, x, entryY, getCategoryWidth(), this.font.lineHeight);

            if (categoryHovered) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }

            int color = (category == current) ? 0xFFFFFFFF : (categoryHovered ? 0xFFFFFFFF : 0xFFADB5C9);

            graphics.centeredText(
                this.font,
                category.getName(),
                x + (getCategoryWidth() / 2),
                entryY,
                color
            );

            index++;
        }
    }

    //menu hovering options
    private void updateCursor(
        GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (isOverSearchBar(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverSocials(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverOpenColorPickerControl(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverColorPickerButton(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverConfigInteraction(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            return;
        }

        if (isOverScrollbar(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    public boolean isOverSocials(int mouseX, int mouseY) {
        return getHoveredSocial(mouseX, mouseY) != null;
    }

    public SocialButton getHoveredSocial(int mouseX, int mouseY) {
        int socialSize = 16;
        int spacing = 8;
        int iconTextSpacing = 4;

        int totalWidth = 0;

        if (selectedCategory != AriesCategory.ABOUT) {
            return null;
        }

        for (SocialButton social : socials) {
            totalWidth += socialSize + iconTextSpacing + this.font.width(Component.literal(social.name())) + spacing;
        }

        totalWidth -= spacing;

        int x = ScreenHelper.centreX(this.width, getMenuWidth());
        int y = ScreenHelper.centreY(this.height, getMenuHeight());

        int contentAreaWidth = getMenuWidth() - getCategoryWidth();

        int centerX = x + getCategoryWidth() + contentAreaWidth / 2;

        // recreate your About layout positions
        int contentY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.25);

        int logoWidth = 48;
        int logoHeight = (int)(logoWidth * (592f / 720f));

        int descriptionY = contentY + logoHeight + PADDING * 4;

        // you need the same description height calculation
        List<FormattedCharSequence> description = this.font.split(
            Component.translatable("aries.description"),
            260
        );

        int infoY = descriptionY + (description.size() * this.font.lineHeight) + PADDING;

        int socialX = centerX - totalWidth / 2;
        int socialY = (int) (infoY + PADDING * 5.25);

        for (SocialButton social : socials) {

            Component name = Component.literal(social.name());

            int width = socialSize
                + iconTextSpacing
                + this.font.width(name);

            if (ScreenHelper.isHovered(
                mouseX, mouseY,
                socialX, socialY,
                width,
                socialSize
            )) {
                return social;
            }

            socialX += width + spacing;
        }

        return null;
    }

    private boolean isOverColorPickerButton(int mouseX, int mouseY) {
        OpenColorPicker colorPicker = getActiveColorPicker();

        if (colorPicker == null) {
            return false;
        }

        int buttonY = ScreenHelper.getButtonY(colorPicker);
        int buttonHeight = ScreenHelper.getButtonHeight();
        int buttonWidth = ScreenHelper.getButtonWidth(colorPicker);
        int buttonGap = ScreenHelper.getButtonGap();

        int copyX = ColorPickerInteraction.getColorPickerHSVX(colorPicker);
        int pasteX = copyX + buttonWidth + buttonGap;
        int resetX = pasteX + buttonWidth + buttonGap;

        return ScreenHelper.isHovered(mouseX, mouseY, copyX, buttonY, buttonWidth, buttonHeight) ||
                ScreenHelper.isHovered(mouseX, mouseY, pasteX, buttonY, buttonWidth, buttonHeight) ||
                ScreenHelper.isHovered(mouseX, mouseY, resetX, buttonY, buttonWidth, buttonHeight);
    }

    public int getColorPickerButtonY(OpenColorPicker picker) {
        return ColorPickerInteraction.getColorPickerAlphaY(picker) + (PADDING / 2 * 3);
    }

    public int getColorPickerButtonHeight() {
        return 16;
    }

    public int getColorPickerButtonGap() {
        return 4;
    }

    public int getColorPickerButtonWidth(OpenColorPicker picker) {
        return (ColorPickerInteraction.getColorPickerHSVWidth(picker) - getColorPickerButtonGap() * 2) / 3;
    }

    boolean isOverSearchBar(int mouseX, int mouseY) {
        int menuX = ScreenHelper.centreX(width, getMenuWidth());
        int menuY = ScreenHelper.centreY(height, getMenuHeight());

        int width = 90;
        int height = 14;

        int x = menuX + getMenuWidth() - AriesScreen.PADDING - width;
        int y = (int)(menuY + AriesScreen.PADDING +
            (this.font.lineHeight - height) / 1.75);

        return ScreenHelper.isHovered(
            mouseX,
            mouseY,
            x,
            y - 1,
            width,
            height
        );
    }

    public void clearSearch() {
        searchBar.clear();
        categoryManuallySelected = false;
        selectedCategory = AriesCategory.ABOUT;
        scrollOffset = 0;
    }

    public void updateSearchCategory() {
        if (!searchBar.hasQuery()) {
            return;
        }

        if (!categoryManuallySelected) {
            AriesCategory bestCategory = getSearchCategory();

            if (bestCategory != null) {
                selectedCategory = bestCategory;
                scrollOffset = 0;
            }
        }
    }

    private AriesCategory getSearchCategory() {
        if (!searchBar.hasQuery()) {
            return selectedCategory;
        }

        String query = searchBar.getText().toLowerCase(Locale.ROOT);

        AriesCategory bestCategory = AriesCategory.ABOUT;
        int bestMatches = 0;

        for (AriesCategory category : AriesCategory.values()) {
            int matches = 0;

            for (AriesFeature feature : AriesFeatures.getFeatures()) {
                if (feature.getCategory() != category || !feature.isVisible()) {
                    continue;
                }

                if (SearchHelper.matches(feature, query)) {
                    matches++;
                }
            }

            if (matches > bestMatches) {
                bestMatches = matches;
                bestCategory = category;
            }
        }

        return bestMatches > 0 ? bestCategory : selectedCategory;
    }

    private boolean isOverOpenColorPickerControl(int mouseX, int mouseY) {
        if (activeColorPicker == null) {
            return false;
        }

        int x = ColorPickerInteraction.getColorPickerHSVX(activeColorPicker);
        int width = ColorPickerInteraction.getColorPickerHSVWidth(activeColorPicker);

        int barHeight = ColorPickerInteraction.getColorPickerBarHeight();

        // HSV square
        if (ScreenHelper.isHovered(
            mouseX,
            mouseY,
            x,
            ColorPickerInteraction.getColorPickerHSVY(activeColorPicker),
            width,
            ColorPickerInteraction.getColorPickerHSVHeight())
        ) {
            return true;
        }

        // Hue bar
        if (ScreenHelper.isHovered(
            mouseX, mouseY, x, ColorPickerInteraction.getColorPickerHueY(activeColorPicker), width, barHeight)
        ) {
            return true;
        }

        // Alpha bar
        //noinspection RedundantIfStatement
        if (ScreenHelper.isHovered(
            mouseX, mouseY, x, ColorPickerInteraction.getColorPickerAlphaY(activeColorPicker), width, barHeight)
        ) {
            return true;
        }

        return false;
    }

    private boolean isOverConfigInteraction(int mouseX, int mouseY) {
        for (ConfigInteraction interaction : configTypeInteractions) {
            if (ScreenHelper.isHovered(
                mouseX, mouseY,
                interaction.x(), interaction.y(),
                interaction.width(), interaction.height()
            )) {
                return true;
            }
        }

        return false;
    }

    private boolean isOverScrollbar(int mouseX, int mouseY) {
        int x = ScreenHelper.centreX(this.width, getMenuWidth());

        int scrollbarX = x + getMenuWidth() - PADDING - SCROLLBAR_WIDTH;

        int visibleHeight =
            AriesScreenLayout.getContentVisibleHeight(this.height, getMenuHeight(), PADDING, this.font.lineHeight);

        int scrollbarHeight = AriesScreenLayout.calculateScrollbarHeight(visibleHeight, PADDING);

        int thumbHeight = AriesScreenLayout.calculateThumbHeight(scrollbarHeight, contentHeight, visibleHeight);

        int thumbY =
            AriesScreenLayout.calculateThumbY(
                AriesScreenLayout.getScrollbarTop(
                    this.height, getMenuHeight(), PADDING, this.font.lineHeight
                ),
                scrollbarHeight,
                thumbHeight,
                scrollOffset,
                contentHeight,
                visibleHeight
            );

        return ScreenHelper.isHovered(mouseX, mouseY, scrollbarX, thumbY, SCROLLBAR_WIDTH, thumbHeight);
    }

    public boolean isOverColorPicker(int mouseX, int mouseY) {
        OpenColorPicker colorPicker = getActiveColorPicker();

        if (colorPicker == null) {
            return false;
        }

        int boxX = colorPicker.getX() - colorPicker.getWidth() / 2;
        int boxY = colorPicker.getY();

        return ScreenHelper.isHovered(
            mouseX, mouseY,
            boxX, boxY,
            colorPicker.getWidth(), colorPicker.getHeight()
        );
    }

    // responsive menu layout dimensions based on current screen resolution
    int getCategoryWidth() {
        return (int) (getMenuWidth() / 3.5);
    }

    int getMenuWidth() {
        return (int) (MENU_WIDTH * getScale());
    }

    int getMenuHeight() {
        return (int) (MENU_HEIGHT * getScale());
    }

    public int getScreenWidth() {
        return width;
    }

    public int getScreenHeight() {
        return height;
    }

    private float getScale() {
        return Mth.clamp(Math.min(this.width / 1920f, this.height / 1080f), 1.0f, 2.0f);
    }

    public AriesCategory getSelectedCategory() {
        return selectedCategory;
    }

    public List<ConfigInteraction> getConfigTypeInteractions() {
        return configTypeInteractions;
    }

    // scrolling
    public int getScrollOffset() {
        return scrollOffset;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public void setSelectedCategory(AriesCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
        this.categoryManuallySelected = true;
        this.scrollOffset = 0;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    // editing states
    public EditState getEditingState() {
        return editingState;
    }

    public void setEditingState(EditState state) {
        editingState = state;
    }

    // keybinds
    public KeybindConfig getListeningKeybind() {
        return listeningKeybind;
    }

    public void setListeningKeybind(KeybindConfig keybind) {
        listeningKeybind = keybind;
    }

    // sliders
    public ConfigInteraction getActiveSlider() {
        return activeSlider;
    }

    public void setActiveSlider(ConfigInteraction slider) {
        activeSlider = slider;
    }

    // color picker
    public OpenColorPicker getActiveColorPicker() {
        return activeColorPicker;
    }

    public void closeColorPicker() {
        activeColorPicker = null;
    }

    // list picker
    public OpenListPicker getActiveListPicker() {
        return activeListPicker;
    }

    public void closeListPicker() {
        activeListPicker = null;
    }

    // search bar blinking
    @Override
    public void tick() {
        searchBar.tick();
    }

    // menu saving
    @Override
    public void removed() {
        AriesScreenCache.savedScrollPosition = scrollOffset;
        AriesScreenCache.category = selectedCategory == AriesCategory.CHANGELOG ? AriesCategory.ABOUT : selectedCategory;
        AriesScreenCache.savedSearchText = searchBar.getText();

        super.removed();
    }

    // keyboard handlers
    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (keyboardHandling.charTyped(event)) {
            return true;
        }

        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (keyboardHandling.keyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    // mouse handlers
    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean handled) {
        if (mouseHandling.mouseClicked(event)) {
            return true;
        }

        return super.mouseClicked(event, handled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (mouseHandling.mouseDragged(event)) {
            return true;
        }

        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (mouseHandling.mouseReleased()) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseHandling.mouseScrolled(mouseX, mouseY, scrollY)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}

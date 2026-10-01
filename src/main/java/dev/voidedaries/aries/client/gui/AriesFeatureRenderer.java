package dev.voidedaries.aries.client.gui;

import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.entry.FeatureEntry;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.AriesConfigType;
import dev.voidedaries.aries.client.render.feature.ConfigInteraction;
import dev.voidedaries.aries.client.render.feature.ConfigTypeRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class AriesFeatureRenderer {
    private final AriesScreen screen;

    public AriesFeatureRenderer(AriesScreen screen) {
        this.screen = screen;
    }

    public void drawMenu(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = ScreenHelper.centreX(screen.width, screen.getMenuWidth());
        int y = ScreenHelper.centreY(screen.height, screen.getMenuHeight());

        AriesCategory currentCategory;

        if (screen.getSearchBar().hasQuery()) {
            currentCategory = screen.getSearchCategory();
        } else {
            currentCategory = screen.getSelectedCategory() != null ? screen.getSelectedCategory() : AriesCategory.ABOUT;
        }

        // starting position below the menu header/title
        int contentY = (int) (y + AriesScreen.PADDING + screen.getFont().lineHeight + AriesScreen.PADDING * 1.5);

        int entryContentY = contentY;
        int scrollOffset = screen.getScrollOffset();

        int contentX = x + screen.getCategoryWidth() + AriesScreen.PADDING * 2;
        int configRenderX = x + screen.getMenuWidth() - AriesScreen.PADDING * 2;

        if (currentCategory == AriesCategory.DEV) {
            entryContentY = drawDevWarning(graphics, x, entryContentY);
        }

        // scissor bounds
        int contentLeft = x + screen.getCategoryWidth() + AriesScreen.PADDING;
        int contentTop = contentY - AriesScreen.PADDING;
        int contentRight = x + screen.getMenuWidth() - AriesScreen.PADDING;
        int contentBottom = y + screen.getMenuHeight();

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

            if (screen.getSearchBar().hasQuery() && !SearchHelper.matches(feature, screen.getSearchBar().getText())) {
                continue;
            }

            // translating content position into render position
            int renderY = entryContentY - scrollOffset;

            int featureConfigWidth = screen.getConfigWidth(feature.getConfigs());

            int featureDescriptionRight = configRenderX - featureConfigWidth - AriesScreen.PADDING;
            int featureDescriptionWidth = featureDescriptionRight - contentX;

            List<FormattedCharSequence> featureTitle =
                screen.getFont().split(feature.getName(), featureDescriptionWidth);

            for (int line = 0; line < featureTitle.size(); line++) {
                graphics.text(
                    screen.getFont(),
                    featureTitle.get(line),
                    contentX,
                    renderY + line * screen.getFont().lineHeight,
                    0xFFFFFFFF
                );
            }

            int featureTitleHeight = featureTitle.size() * screen.getFont().lineHeight;

            List<FormattedCharSequence> featureDescription = ScreenHelper.splitDescriptionWithScale(
                screen.getFont(), feature.getDescription(), featureDescriptionWidth, AriesScreen.DESCRIPTION_SCALE
            );

            int featureDescriptionHeight =
                drawDescription(
                    graphics,
                    featureDescription,
                    contentX,
                    renderY + featureTitleHeight + AriesScreen.PADDING / 2
                );

            int featureTextHeight = featureTitleHeight + featureDescriptionHeight;

            int featureConfigHeight = screen.getConfigHeight(feature.getConfigs());

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
                    screen.getConfigTypeInteractions().addAll(interactions);

                    if (!interactions.isEmpty()) {
                        int maxHeight = interactions.stream().mapToInt(ConfigInteraction::height).max().orElse(0);
                        featureConfigY += maxHeight + AriesScreen.PADDING * 2;
                    }
                }
            }

            // tracks the next available vertical position in the content layout
            int nextContentY = entryContentY;
            nextContentY += Math.max(featureTextHeight, featureConfigHeight) + AriesScreen.FEATURE_SPACING * 4;

            // drawing entries for feature specific sub-configs
            for (FeatureEntry entry : feature.getEntries()) {
                if (!entry.isVisible()) {
                    continue;
                }

                int entryDrawY = nextContentY - scrollOffset;

                int entryConfigWidth = screen.getConfigWidth(entry.configs());

                int entryDescriptionRight = configRenderX - entryConfigWidth - AriesScreen.PADDING;
                int entryDescriptionWidth = entryDescriptionRight - contentX;

                List<FormattedCharSequence> entryTitle = screen.getFont().split(entry.name(), entryDescriptionWidth);

                for (int line = 0; line < entryTitle.size(); line++) {
                    graphics.text(
                        screen.getFont(),
                        entryTitle.get(line),
                        contentX,
                        entryDrawY + line * screen.getFont().lineHeight,
                        0xFFFFFFFF
                    );
                }

                int entryTitleHeight = entryTitle.size() * screen.getFont().lineHeight;

                List<FormattedCharSequence> entryDescription = ScreenHelper.splitDescriptionWithScale(
                    screen.getFont(), entry.description(), entryDescriptionWidth, AriesScreen.DESCRIPTION_SCALE
                );

                int descriptionHeight = drawDescription(
                    graphics,
                    entryDescription,
                    contentX,
                    entryDrawY + entryTitleHeight + AriesScreen.PADDING / 2
                );

                int textHeight = entryTitleHeight + descriptionHeight;

                int configHeight = screen.getConfigHeight(entry.configs());

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
                        screen.getConfigTypeInteractions().addAll(interactions);

                        if (!interactions.isEmpty()) {
                            int maxHeight =
                                interactions.stream().mapToInt(ConfigInteraction::height).max().orElse(0);
                            configRenderY += maxHeight + AriesScreen.PADDING * 2;
                        }
                    }
                }

                // move the layout cursor down by the entry's height
                nextContentY += Math.max(textHeight, configHeight) + AriesScreen.ENTRY_SPACING * 4;
            }

            // position entries below the feature
            entryContentY = nextContentY + AriesScreen.FEATURE_SPACING;
        }

        // calculate total scrollable content height
        int contentHeight = entryContentY - contentY;
        scrollOffset = AriesScreenLayout.clampScroll(scrollOffset, contentHeight, visibleHeight);

        screen.setScrollOffset(scrollOffset);
        screen.setContentHeight(contentHeight);

        graphics.disableScissor();

        boolean needsScrollbar = contentHeight > visibleHeight;

        // scrollbar handling
        if (needsScrollbar) {
            screen.drawScrollbar(
                graphics,
                contentRight - AriesScreen.SCROLLBAR_WIDTH,
                contentTop + AriesScreen.PADDING,
                (contentBottom - contentTop) - (AriesScreen.PADDING * 2),
                visibleHeight
            );
        }
    }

    private int drawDevWarning(GuiGraphicsExtractor graphics, int x, int y) {
        Component warning = Component.translatable("gui.menu.category.dev.warning");

        float warningScale = 0.85f;

        List<FormattedCharSequence> warningLines =
            screen.getFont().split(warning, (screen.getMenuWidth() - screen.getCategoryWidth()));

        int warningHeight = 0;

        for (FormattedCharSequence warningLine : warningLines) {
            graphics.pose().pushMatrix();

            graphics.pose().translate(x + screen.getCategoryWidth() + AriesScreen.PADDING * 2 , y + warningHeight);
            graphics.pose().scale(warningScale, warningScale);

            graphics.text(screen.getFont(), warningLine, 0, 0, 0xFFFF5555);

            graphics.pose().popMatrix();

            warningHeight += (int) (screen.getFont().lineHeight * warningScale);
        }

        return y + warningHeight + AriesScreen.PADDING;
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
            screen.getFont(),
            config,
            controlRightX,
            configY,
            mouseX, mouseY,

            screen.getEditingState(),
            screen.getActiveColorPicker(),
            screen.getListeningKeybind(),

            screen::setEditingState,
            screen::setActiveColorPicker,
            screen::setActiveListPicker,
            screen::setListeningKeybind
        );
    }

    private int drawDescription(GuiGraphicsExtractor graphics, List<FormattedCharSequence> lines, int x, int y) {
        float scale = AriesScreen.DESCRIPTION_SCALE;

        // description text
        for (int line = 0; line < lines.size(); line++) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y + line * screen.getFont().lineHeight);
            graphics.pose().scale(scale, scale);
            graphics.text(screen.getFont(), lines.get(line), 0, 0, 0xFFADB5C9);
            graphics.pose().popMatrix();
        }

        return (int) (lines.size() * screen.getFont().lineHeight * scale);
    }

}

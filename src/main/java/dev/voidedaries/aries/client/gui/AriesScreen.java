package dev.voidedaries.aries.client.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.voidedaries.aries.ModConstants;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.category.AriesCategory;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.feature.config.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AriesScreen extends Screen {
    private static final String version = FabricLoader.getInstance()
        .getModContainer(ModConstants.MOD_ID)
        .map(
            mod -> mod.getMetadata().getVersion().getFriendlyString()
        ).orElse("unknown");

    private static final String displayVersion = version.contains("-")
        ? version.substring(0, version.indexOf("-"))
        : version;

    private static final int MENU_WIDTH = 360;
    private static final int MENU_HEIGHT = 220;

    private static final int PADDING = 10;

    private boolean authorHovered = false;

    private ConfigInteraction activeSlider = null;

    private static AriesCategory selectedCategory = AriesCategory.ABOUT;

    private final List<ConfigInteraction> configTypeInteractions = new ArrayList<>();

    public AriesScreen() {
        super(Component.literal(ModConstants.MOD_ID));
    }

    @Override
    protected void init() {
        configTypeInteractions.clear();
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        configTypeInteractions.clear();

        int x = (this.width - getMenuWidth()) / 2;
        int y = (this.height - getMenuHeight()) / 2;

        graphics.fill(x, y, x + getMenuWidth(), y + getMenuHeight(), 0xFF222933);

        graphics.fill(x, y, x + getCategoryWidth(), y + getMenuHeight(), 0xFF151A21);

        drawMenu(graphics, mouseX, mouseY, delta);

        drawCategories(graphics, mouseX, mouseY, delta);

        for (ConfigInteraction interaction : configTypeInteractions) {
            boolean hovered =
                mouseX >= interaction.x() &&
                    mouseX <= interaction.x() + interaction.width() &&
                    mouseY >= interaction.y() &&
                    mouseY <= interaction.y() + interaction.height();

            if (hovered) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
                break;
            }
        }
    }

    private void drawMenu(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = (this.width - getMenuWidth()) / 2;
        int y = (this.height - getMenuHeight()) / 2;

        int contentX = x + getCategoryWidth() + PADDING * 2;
        int contentY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        AriesCategory currentCategory = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        int index = 0;

        int authorX = x + getCategoryWidth() + PADDING;
        int authorY = y + PADDING;

        int titleTextWidth = this.font.width("Aries • ");
        int authorWidth = this.font.width(Component.translatable("authors.dev.voidedaries"));

        authorHovered = mouseX >= authorX + titleTextWidth && mouseX <= authorX + titleTextWidth + authorWidth
            && mouseY >= authorY && mouseY <= authorY + this.font.lineHeight;

        if (authorHovered) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }

        graphics.drawString(
            this.font,
            Component.literal("Aries")
                .append(Component.literal(" • ").withColor(0xFFADB5C9))
                .append(Component.translatable("authors.dev.voidedaries")
                    .withStyle(s -> authorHovered ? s.withUnderlined(true) : s))
                .append(Component.literal(" • ").withColor(0xFFADB5C9))
                .append(Component.literal(displayVersion)),
            x + getCategoryWidth() + 2 * PADDING,
            y + PADDING,
            0xFF0058E1
        );

        graphics.hLine(
            x + getCategoryWidth() + PADDING,
            x + getMenuWidth() - PADDING,
            y + PADDING + this.font.lineHeight + PADDING / 2,
            0xFF2D3642
        );

        int entryY = contentY;

        // drawing features
        for (AriesFeature feature : AriesFeatures.getFeatures()) {
            if (feature.getCategory() != currentCategory) {
                continue;
            }

            // name
            graphics.drawString(
                this.font,
                feature.getName(),
                contentX,
                entryY,
                0xFFFFFFFF
            );

            // description max width
            int descMaxWidth = (int) ((getMenuWidth() - getCategoryWidth()) / 1.5);

            List<FormattedCharSequence> descLines = this.font.split(
                Component.literal(feature.getDescription().getString()), descMaxWidth
            );

            // description text
            for (int line = 0; line < descLines.size(); line++) {
                float descScale = 0.80f;
                graphics.pose().pushMatrix();
                graphics.pose().translate(
                    contentX,
                    entryY + this.font.lineHeight + (float) PADDING / 2 + line * this.font.lineHeight
                );
                graphics.pose().scale(descScale, descScale);
                graphics.drawString(this.font, descLines.get(line), 0, 0, 0xFFADB5C9);
                graphics.pose().popMatrix();
            }

            int controlRightX = x + getMenuWidth() - PADDING * 2;

            // config type rendering
            for (AriesConfigType<?> config : feature.getConfigs()) {
                switch (config.getType()) {
                    case TOGGLE -> configTypeInteractions.add(
                        ConfigTypeRenderer.drawToggle(graphics, this.font, config, controlRightX, entryY)
                    );
                    case SLIDER -> configTypeInteractions.add(
                        ConfigTypeRenderer.drawSlider(graphics, this.font, config, controlRightX, entryY)
                    );
                }
            }

            index++;

            int featureHeight =
                this.font.lineHeight + (int)(descLines.size() * this.font.lineHeight * 0.8f) + PADDING * 2;

            entryY += featureHeight;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean handled) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();

        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }

        if (authorHovered) {
            Util.getPlatform().openUri("https://github.com/voidedaries/aries-mod");
        }

        int x = (this.width - getMenuWidth()) / 2;
        int y = (this.height - getMenuHeight()) / 2;

        int startY = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        int i = 0;

        for (AriesCategory category : AriesCategory.values()) {

            int entryY = startY + i * (this.font.lineHeight + PADDING);

            boolean hovered =
                mouseX >= x && mouseX <= x + getCategoryWidth() &&
                    mouseY >= entryY && mouseY <= entryY + this.font.lineHeight;

            if (hovered) {
                selectedCategory = category;
                return true;
            }

            i++;
        }

        for (ConfigInteraction interaction : configTypeInteractions) {
            boolean hovered = mouseX >= interaction.x() && mouseX <= interaction.x() + interaction.width()
                && mouseY >= interaction.y() && mouseY <= interaction.y() + interaction.height();

            if (!hovered) {
                continue;
            }

            if (interaction.config() instanceof BooleanConfig bool) {
                bool.set(!bool.get());
                AriesConfig.save();
                return true;
            }

            if (interaction.config() instanceof IntConfig) {
                activeSlider = interaction;
                updateSlider(mouseX, interaction);
                AriesConfig.save();
                return true;
            }
        }

        return super.mouseClicked(event, handled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (activeSlider != null) {
            updateSlider((int) mouseX, activeSlider);
            return true;
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        activeSlider = null;
        return super.mouseReleased(event);
    }

    private void drawCategories(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float ignoredDelta) {
        int x = (this.width - getMenuWidth()) / 2;
        int y = (this.height - getMenuHeight()) / 2;

        int startCategoryHeight = (int) (y + PADDING + this.font.lineHeight + PADDING * 1.5);

        graphics.drawCenteredString(
                this.font,
                Component.translatable("gui.menu.categories"),
                x + (getCategoryWidth() / 2),
                y + PADDING,
                0xFF0058E1
        );

        graphics.hLine(
                x + PADDING,
                x + getCategoryWidth() - PADDING,
                y + PADDING + this.font.lineHeight + PADDING / 2,
                0xFF2D3642
        );

        AriesCategory current = selectedCategory != null ? selectedCategory : AriesCategory.ABOUT;

        int index = 0;

        for (AriesCategory category : AriesCategory.values()) {
            int entryY = startCategoryHeight + index * (this.font.lineHeight + PADDING);

            boolean hovered =
                mouseX >= x && mouseX <= x + getCategoryWidth() &&
                    mouseY >= entryY && mouseY <= entryY + this.font.lineHeight;

            if (hovered) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }

            int color = (category == current)
                ? 0xFFFFFFFF
                : (hovered ? 0xFFFFFFFF : 0xFFADB5C9);

            graphics.drawCenteredString(
                    this.font,
                    category.getName(),
                    x + (getCategoryWidth() / 2),
                    entryY,
                    color
            );

            index++;
        }
    }

    private float getScale() {
        return Mth.clamp(Math.min(this.width / 1920f, this.height / 1080f), 1.0f, 2.0f);
    }

    private void updateSlider(int mouseX, ConfigInteraction interaction) {
        if (!(interaction.config() instanceof IntConfig slider)) {
            return;
        }

        int x = interaction.x();
        int width = interaction.width();

        float percent = (mouseX - x) / (float) width;
        percent = Mth.clamp(percent, 0f, 1f);

        int value = slider.getMin() + Math.round(percent * (slider.getMax() - slider.getMin()));

        slider.set(value);
    }

    private int getCategoryWidth() {
        return (int) (getMenuWidth() / 3.5);
    }

    private int getMenuWidth() {
        return (int) (MENU_WIDTH * getScale());
    }

    private int getMenuHeight() {
        return (int) (MENU_HEIGHT * getScale());
    }

}

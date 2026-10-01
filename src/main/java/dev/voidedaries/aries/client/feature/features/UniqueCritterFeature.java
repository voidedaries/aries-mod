package dev.voidedaries.aries.client.feature.features;

import com.mojang.blaze3d.textures.GpuTexture;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.feature.types.AriesCategory;
import dev.voidedaries.aries.client.feature.types.BooleanConfig;
import dev.voidedaries.aries.client.feature.types.ColorConfig;
import dev.voidedaries.aries.client.feature.types.IntConfig;
import dev.voidedaries.aries.client.gui.location.HudBounds;
import dev.voidedaries.aries.client.gui.location.HudPosition;
import dev.voidedaries.aries.client.gui.location.HudRenderable;
import dev.voidedaries.aries.client.render.gif.GifManager;
import dev.voidedaries.aries.skyblock.entity.Critter;
import dev.voidedaries.aries.skyblock.entity.CritterBiome;
import dev.voidedaries.aries.skyblock.entity.CritterSafari;
import dev.voidedaries.aries.skyblock.entity.CritterSafariChatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

public class UniqueCritterFeature extends AriesFeature implements HudRenderable {
    public final BooleanConfig enabled = addConfig(new BooleanConfig("unique_critter.enabled", true));
    public final BooleanConfig show_only_current_biome =
        new BooleanConfig("show_current_biome_only.enabled", true);

    public final ColorConfig caught_color = new ColorConfig("caught_color", 0xFF55FF55);
    public final ColorConfig not_caught_color = new ColorConfig("not_caught_color", 0xFFFF5555);

    public final IntConfig hud_critter_columns = new IntConfig("unique_critter_columns", 3, 1, 5);

    private final HudPosition hudPosition = new HudPosition();

    public static final int DISPLAY_SIZE = 16;

    private static final int PADDING = 5;
    private static final int NAME_PADDING = PADDING / 2;

    public UniqueCritterFeature() {
        super(
            Component.translatable("gui.category.critter_safari.unique_critter.name"),
            Component.translatable("gui.category.critter_safari.unique_critter.description"),
            AriesCategory.CRITTER_SAFARI
        );

        addEntry(
            Component.translatable("gui.category.critter_safari.unique_critter.show_current_biome_only.name"),
            Component.translatable("gui.category.critter_safari.unique_critter.show_current_biome_only.description"),
            show_only_current_biome
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.critter_safari.unique_critter.unique_critter_columns.name"),
            Component.translatable("gui.category.critter_safari.unique_critter.unique_critter_columns.description"),
            hud_critter_columns
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.critter_safari.unique_critter.caught_color.name"),
            Component.translatable("gui.category.critter_safari.unique_critter.caught_color.description"),
            caught_color
        ).visibleWhen(enabled::get);

        addEntry(
            Component.translatable("gui.category.critter_safari.unique_critter.not_caught_color.name"),
            Component.translatable("gui.category.critter_safari.unique_critter.not_caught_color.description"),
            not_caught_color
        ).visibleWhen(enabled::get);
    }

    private static int[] getScaledSize(GpuTexture texture) {
        int width = texture.getWidth(0);
        int height = texture.getHeight(0);

        if (width >= height) {
            return new int[] {
                DISPLAY_SIZE,
                Math.round((float) height / width * DISPLAY_SIZE)
            };
        }

        return new int[] {
            Math.round((float) width / height * DISPLAY_SIZE),
            DISPLAY_SIZE
        };
    }

    @Override
    public String getHudId() {
        return "unique_critter";
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
        CritterSafari safari = CritterSafariChatHandler.getSafari();

        if (!safari.isActive()) {
            return new HudBounds(x, y, 0, 0);
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return new HudBounds(x, y, 0, 0);
        }

        Holder<Biome> playerBiome = minecraft.level.getBiome(minecraft.player.blockPosition());

        Identifier currentBiomeId =
            minecraft.level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(playerBiome.value());

        if (currentBiomeId == null) {
            return new HudBounds(x, y, 0, 0);
        }

        CritterBiome currentBiome = CritterBiome.fromIdentifier(currentBiomeId);

        String title = "Unique Critters";

        int currentY = y + minecraft.font.lineHeight + PADDING;
        int hudWidth = minecraft.font.width(title);
        int hudHeight = minecraft.font.lineHeight;

        for (CritterBiome biome : CritterBiome.values()) {

            if (show_only_current_biome.get() && biome != currentBiome) {
                continue;
            }

            String biomeText = biome.toString().substring(0, 1).toUpperCase()
                + biome.toString().substring(1).toLowerCase();

            graphics.text(
                minecraft.font,
                biomeText,
                x,
                currentY,
                0xFFFFFFFF
            );

            hudWidth = Math.max(hudWidth, minecraft.font.width(biomeText));
            currentY += minecraft.font.lineHeight + PADDING;

            int column = 0;
            int row = 0;
            int critterCount = 0;
            int currentX = x;

            for (Critter critter : Critter.values()) {
                if (critter.getBiome() != biome) {
                    continue;
                }

                critterCount++;

                boolean caught = safari.isCaught(critter);

                int rowY = currentY + row * (DISPLAY_SIZE + PADDING);


                Identifier texture = GifManager.getCritterTexture(critter);

                GpuTexture gpuTexture = minecraft.getTextureManager().getTexture(texture).getTexture();

                int textureWidth = gpuTexture.getWidth(0);
                int textureHeight = gpuTexture.getHeight(0);

                int[] scaledSize = getScaledSize(gpuTexture);

                int textureX = currentX + (DISPLAY_SIZE - scaledSize[0]) / 2;
                int textureY = rowY + (DISPLAY_SIZE - scaledSize[1]) / 2;

                graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    textureX, textureY,
                    0, 0,
                    scaledSize[0], scaledSize[1],
                    textureWidth, textureHeight,
                    textureWidth, textureHeight
                );

                int nameY = rowY + (DISPLAY_SIZE - minecraft.font.lineHeight) / 2;
                int nameColor = caught ? caught_color.get() : not_caught_color.get();

                graphics.text(
                    minecraft.font,
                    critter.getName(),
                    currentX + DISPLAY_SIZE + NAME_PADDING,
                    nameY,
                    nameColor
                );

                currentX += DISPLAY_SIZE + NAME_PADDING + minecraft.font.width(critter.getName()) + PADDING;
                hudWidth = Math.max(hudWidth, currentX - x);

                column++;

                if (column >= hud_critter_columns.get()) {
                    column = 0;
                    row++;
                    currentX = x;
                }
            }

            int rows = (critterCount + hud_critter_columns.get() - 1) / hud_critter_columns.get();
            currentY += rows * (DISPLAY_SIZE + PADDING) + PADDING;
            hudHeight = currentY - y;
        }

        graphics.text(
            minecraft.font,
            title,
            x, y,
            0xFFFFFFFF
        );

        return new HudBounds(x, y, hudWidth, hudHeight);
    }

    @Override
    public HudBounds getHudBounds() {
        Minecraft minecraft = Minecraft.getInstance();

        String text = "Unique Critters";

        return new HudBounds(0, 0, minecraft.font.width(text), minecraft.font.lineHeight);
    }
}

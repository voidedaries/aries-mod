package dev.voidedaries.aries.client.render.gif;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.awt.image.BufferedImage;

public class GifTexture {
    private final Identifier identifier;
    private final DynamicTexture texture;

    public GifTexture(Identifier identifier, DynamicTexture texture) {
        this.identifier = identifier;
        this.texture = texture;
    }

    public GifTexture(Identifier identifier, BufferedImage image) {
        this.identifier = identifier;

        NativeImage nativeImage = toNativeImage(image);

        this.texture = new DynamicTexture(identifier::toString, nativeImage);
    }

    public void register() {
        Minecraft.getInstance().getTextureManager().register(identifier, texture);
    }

    public void update(BufferedImage image) {
        texture.setPixels(toNativeImage(image));
        texture.upload();
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    private static NativeImage toNativeImage(BufferedImage image) {
        NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), false);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                nativeImage.setPixel(
                    x,
                    y,
                    image.getRGB(x, y)
                );
            }
        }

        return nativeImage;
    }

}

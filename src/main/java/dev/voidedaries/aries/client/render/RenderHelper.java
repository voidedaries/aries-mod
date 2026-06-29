package dev.voidedaries.aries.client.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public class RenderHelper {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static Camera getCamera() {
        return CLIENT.gameRenderer.getMainCamera();
    }

}

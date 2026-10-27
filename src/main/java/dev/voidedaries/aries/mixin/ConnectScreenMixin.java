package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.hypixel.ResourcePackServerState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "startConnecting", at = @At("HEAD"))
    private static void aries$recordTargetServer(
        Screen parent,
        Minecraft minecraft,
        ServerAddress hostAndPort,
        ServerData data,
        boolean isQuickPlay,
        @Nullable TransferState transferState,
        CallbackInfo ci
    ) {
        ResourcePackServerState.setServerAddress(data.ip);
    }

}

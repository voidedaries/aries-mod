package dev.voidedaries.aries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Pattern;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Unique
    private static final Pattern ASHFANG_DEATH = Pattern.compile("^ ☠ You were killed by Ashfang(?: .+)?\\.$");

    @WrapOperation(method = "handleSetEntityPassengersPacket", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"))
    private void aries$suppressPassengerSpam(Logger instance, String s, Operation<Void> original) {
        if (AriesFeatures.MINECRAFT_WARNING_LOG_SUPPRESSION.isEnabled()
            && HypixelState.isPlayerInSkyblock()
            && s.equals("Received passengers for unknown entity")
        ) {
            return;
        }

        original.call(instance, s);
    }

    @Inject(method = "handleSystemChat", at = @At("HEAD"))
    private void aries$detectAshfangDeath(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        String message = packet.content().getString();

        if (AriesFeatures.ASHFANG_DEATH_MODE.isEnabled()
            && ASHFANG_DEATH.matcher(message).matches()) {
            Minecraft.getInstance().emergencySaveAndCrash(
                new CrashReport("☠ You died to Ashfang", new RuntimeException("Skill Issue"))
            );
        }
    }

    @Inject(method = "verifyCommand", at = @At("RETURN"), cancellable = true)
    private void aries$skipCommandConfirm(
        String command,
        CallbackInfoReturnable<ClientPacketListener.CommandCheckResult> cir
    ) {
        if (!AriesFeatures.SKIP_COMMAND_CONFIRM.isEnabled()
            && !AriesFeatures.HYPIXEL_ENVIRONMENT_OVERRIDE.isEnabled()) {
            return;
        }

        if (!HypixelState.isOnHypixelNetwork()) {
            return;
        }

        ClientPacketListener.CommandCheckResult result = cir.getReturnValue();

        // Do not bypass signed commands.
        if (result == ClientPacketListener.CommandCheckResult.SIGNATURE_REQUIRED) {
            return;
        }

        // Allows Hypixel servers with incomplete command trees
        // to handle commands instead of showing the confirmation screen
        if (result == ClientPacketListener.CommandCheckResult.PERMISSIONS_REQUIRED
            || result == ClientPacketListener.CommandCheckResult.PARSE_ERRORS) {
            cir.setReturnValue(ClientPacketListener.CommandCheckResult.NO_ISSUES);
        }

    }

}

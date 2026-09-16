package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
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
    private static final Pattern ASHFANG_DEATH = Pattern.compile("^ ☠ You were killed by Ashfang\\.$");

    @Inject(method = "handleSetEntityPassengersPacket", at = @At("HEAD"), cancellable = true)
    private void aries$suppressPassengerSpam(ClientboundSetPassengersPacket packet, CallbackInfo ci) {
        if (AriesFeatures.MINECRAFT_WARNING_LOG_SUPPRESSION.isEnabled() && HypixelState.isPlayerInSkyblock()) {
            ci.cancel();
        }
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

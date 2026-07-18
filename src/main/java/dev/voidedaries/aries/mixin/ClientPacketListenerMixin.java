package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "verifyCommand", at = @At("RETURN"), cancellable = true)
    private void aries$skipCommandConfirm(
        String command,
        CallbackInfoReturnable<ClientPacketListener.CommandCheckResult> cir
    ) {
        if (!AriesFeatures.SKIP_COMMAND_CONFIRM.enabled.get()) {
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

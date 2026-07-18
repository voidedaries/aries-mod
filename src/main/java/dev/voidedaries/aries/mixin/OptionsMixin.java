package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.AriesConfig;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "save", at = @At("TAIL"))
    private void aries$saveConfig(CallbackInfo ci) {
        AriesConfig.save();
    }

}

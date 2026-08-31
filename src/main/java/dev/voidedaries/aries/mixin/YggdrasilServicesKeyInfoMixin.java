package dev.voidedaries.aries.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(YggdrasilServicesKeyInfo.class)
public class YggdrasilServicesKeyInfoMixin {

    @WrapOperation(method = "validateProperty", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private void aries$suppressYggdrasilSpam(
        Logger logger,
        String message,
        Object property,
        Object exception,
        Operation<Void> original
    ) {
        if (!AriesFeatures.YGGDRASIL_LOG_SUPPRESSION.isEnabled() || !HypixelState.isOnHypixelNetwork()) {
            original.call(logger, message, property, exception);
        }
    }

}

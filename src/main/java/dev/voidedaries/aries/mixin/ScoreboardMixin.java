package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {

    @Redirect(method = "addPlayerTeam", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void aries$suppressTeamCreationSpam(Logger instance, String s, Object o) {
        if (!AriesFeatures.MINECRAFT_WARNING_LOG_SUPPRESSION.isEnabled()
            && !HypixelState.isPlayerInSkyblock()) {
            instance.warn(s, o);
        }
    }

}

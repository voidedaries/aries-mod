package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.skyblock.item.SkyblockLevelGradient;
import dev.voidedaries.aries.skyblock.item.SkyblockLevelTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "getNameTag", at = @At("RETURN"), cancellable = true)
    private void aries$addTestSkyBlockLevel(T entity, CallbackInfoReturnable<Component> cir) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        if (entity != minecraft.player) {
            return;
        }

        if (!AriesFeatures.HYPIXEL_ENVIRONMENT_OVERRIDE.isEnabled()) {
            return;
        }

        Component original = cir.getReturnValue();

        if (original == null) {
            return;
        }

        int testLevel = 520;

        SkyblockLevelTier tier = SkyblockLevelTier.getForLevel(testLevel);
        SkyblockLevelGradient gradient = tier.getGradient();

        MutableComponent levelComponent = Component.empty();

        levelComponent.append(Component.literal("["));

        String level = String.valueOf(testLevel);

        long time = System.currentTimeMillis();

        for (int index = 0; index < level.length(); index++) {
            double position = (double) index / Math.max(1, level.length() - 1);

            int color = gradient.getAnimatedColor(position, time);

            levelComponent.append(
                Component.literal(String.valueOf(level.charAt(index)))
                    .withStyle(style -> style.withColor(color))
            );
        }

        levelComponent.append(Component.literal("] "));

        cir.setReturnValue(levelComponent.append(original));
    }

}

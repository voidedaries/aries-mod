package dev.voidedaries.aries.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidedaries.aries.client.render.item.EtherwarpRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(
        //? if >=26.2
        //method = "submitBlockOutline",
        //? if <26.2
        method = "extractBlockOutline",
        at = @At("HEAD"),
        cancellable = true)
    private void aries$hideVanillaOutline(
        //? if >= 26.2
        //PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LevelRenderState levelRenderState, CallbackInfo ci
        //? if <26.2
        Camera camera, LevelRenderState levelRenderState, CallbackInfo ci
    ) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack item = player.getMainHandItem();

        if (EtherwarpRenderer.isActive() && EtherwarpRenderer.isEtherwarpItem(item)) {
            ci.cancel();
        }
    }

}

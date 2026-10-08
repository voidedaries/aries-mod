package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.accessor.AvatarRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateAccessor {

    @Unique
    private boolean aries$isLocalPlayer;

    @Override
    public boolean aries$isLocalPlayer() {
        return aries$isLocalPlayer;
    }

    @Override
    public void aries$setLocalPlayer(boolean value) {
        aries$isLocalPlayer = value;
    }

}

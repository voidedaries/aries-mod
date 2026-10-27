package dev.voidedaries.aries.mixin;

import net.minecraft.client.resources.server.ServerPackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerPackManager.class)
public interface ServerPackManagerAccessor {

    @Accessor("packs")
    List<?> aries$getPacks();

}

package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.resourcepack.PersistentHypixelPackSource;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Arrays;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>([Lnet/minecraft/server/packs/repository/RepositorySource;)V"))
    private void aries$addPersistentHypixelPackSource(Args args) {
        RepositorySource[] existing = args.get(0);
        RepositorySource[] updated = Arrays.copyOf(existing, existing.length + 1);

        updated[existing.length] = new PersistentHypixelPackSource();
        args.set(0, updated);
    }

}

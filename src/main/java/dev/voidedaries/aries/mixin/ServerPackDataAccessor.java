package dev.voidedaries.aries.mixin;

import com.google.common.hash.HashCode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
import java.util.UUID;

@Mixin(targets = "net.minecraft.client.resources.server.ServerPackManager$ServerPackData")
public interface ServerPackDataAccessor {

    @Accessor("id")
    UUID aries$getId();

    @Accessor("hash")
    HashCode aries$getHash();

    @Accessor("path")
    Path aries$getPath();

}

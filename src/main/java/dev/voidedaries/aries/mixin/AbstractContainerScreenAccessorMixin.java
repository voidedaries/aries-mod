package dev.voidedaries.aries.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessorMixin {

    @Accessor("leftPos")
    int aries$getLeftPos();

    @Accessor("topPos")
    int aries$getTopPos();

}

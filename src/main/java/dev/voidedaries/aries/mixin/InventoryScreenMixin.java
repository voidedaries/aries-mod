package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("TAIL"))
    private void aries$addArmorButtons(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY,
        float a,
        CallbackInfo ci
    ) {
        if (!AriesFeatures.HIDE_ARMOR.isEnabled()) {
            return;
        }

        int x = ((AbstractContainerScreenAccessorMixin) this).aries$getLeftPos();
        int y = ((AbstractContainerScreenAccessorMixin) this).aries$getTopPos();

        renderArmorButton(graphics, x + 17, y + 8, EquipmentSlot.HEAD);
        renderArmorButton(graphics, x + 17, y + 26, EquipmentSlot.CHEST);
        renderArmorButton(graphics, x + 17, y + 44, EquipmentSlot.LEGS);
        renderArmorButton(graphics, x + 17, y + 62, EquipmentSlot.FEET);
    }

    @Unique
    private void renderArmorButton(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        EquipmentSlot slot
    ) {
        boolean hidden = AriesFeatures.HIDE_ARMOR.isHidden(slot);

        Identifier texture = hidden
            ? Aries.id("textures/gui/eye_closed.png")
            : Aries.id("textures/gui/eye_open.png");

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            x,
            y,
            0,
            0,
            7,
            5,
            7,
            5
        );
    }

}

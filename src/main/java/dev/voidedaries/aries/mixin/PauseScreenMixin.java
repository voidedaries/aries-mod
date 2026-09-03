package dev.voidedaries.aries.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.gui.AriesScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.layouts.LinearLayout;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @Unique
    private static final int BUTTON_SIZE = 20;
    @Unique
    private static final int SPRITE_SIZE = 16;

    //? if >= 26.2 {
    @Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0))
    private void ariesPauseMenuButton(CallbackInfo ci, @Local(name = "iconButtonRow") LinearLayout iconButtonRow) {
        if (!AriesFeatures.PAUSE_MENU_BUTTON.isEnabled()) {
            return;
        }

        SpriteIconButton ariesButton = SpriteIconButton.builder(
            Component.translatable("aries.mod_name"),
            _ ->
                Minecraft.getInstance().gui.setScreen(new AriesScreen()),
            true
        ).width(BUTTON_SIZE).sprite(Aries.id("logo_icon"), SPRITE_SIZE, SPRITE_SIZE).withTootip().build();

        iconButtonRow.addChild(ariesButton);
    }
    //?}

        //? if <26.2 {
    /*@Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0))
    private void ariesPauseMenuButton(CallbackInfo ci, @Local(name = "helper") GridLayout.RowHelper helper) {
        if (!AriesFeatures.PAUSE_MENU_BUTTON.isEnabled()) {
            return;
        }

        helper.addChild(
            Button.builder(Component.translatable("aries.mod_name"), _ -> Minecraft.getInstance().setScreen(new AriesScreen()))
                .width(PauseScreen.BUTTON_WIDTH_FULL).build(),2
        );
    }
    *///?}

}

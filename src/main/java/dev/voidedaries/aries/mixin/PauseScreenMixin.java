package dev.voidedaries.aries.mixin;

import com.llamalad7.mixinextras.sugar.Local;
//? if 26.2
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.feature.AriesFeatures;
import dev.voidedaries.aries.client.gui.AriesScreen;
import dev.voidedaries.aries.client.hypixel.HypixelState;
import net.minecraft.client.Minecraft;
//? if 26.1.2
//import net.minecraft.client.gui.components.Button;
//? if 26.2
import net.minecraft.client.gui.components.SpriteIconButton;
//? if 26.1.2
//import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
//? if 26.1.2
//import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.Dialogs;
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

    //? 26.2 {
    @Inject(method = "addCustomDialogButtons", at = @At("HEAD"), cancellable = true)
    private void aries$deleteServerLinksButton(
        Minecraft minecraft,
        Holder<Dialog> dialog,
        GridLayout.RowHelper helper,
        CallbackInfo ci
    ) {
        if (!HypixelState.isOnHypixelNetwork() || minecraft.player == null) {
            return;
        }

        boolean isServerLinks = minecraft.player.connection.registryAccess()
            .lookupOrThrow(Registries.DIALOG)
            .get(Dialogs.SERVER_LINKS)
            .map(serverLinks -> serverLinks.equals(dialog))
            .orElse(false);

        if (isServerLinks) {
            ci.cancel();
        }
    }
    //?}

    //? 26.1.2 {
    /*@Inject(method = "addFeedbackSubscreenAndCustomDialogButtons", at = @At("HEAD"), cancellable = true)
    private void aries$deleteServerLinksButton(
        Minecraft minecraft,
        Holder<Dialog> dialog,
        GridLayout.RowHelper helper,
        CallbackInfo ci
    ) {
        if (!AriesFeatures.HYPIXEL_PAUSE_MENU_BUTTON.isEnabled()
            || !HypixelState.isOnHypixelNetwork()
            || minecraft.player == null) {
            return;
        }

        boolean isServerLinks = minecraft.player.connection.registryAccess()
            .lookupOrThrow(Registries.DIALOG)
            .get(Dialogs.SERVER_LINKS)
            .map(serverLinks -> serverLinks.equals(dialog))
            .orElse(false);

        if (isServerLinks) {
            ci.cancel();
        }
    }
    *///?}

    //? if 26.2 {
    @Inject(
        method = "createPauseMenu",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
            ordinal = 0
        )
    )
    private void ariesPauseMenuButton(
        CallbackInfo ci,
        @Local(name = "iconButtonRow") LinearLayout iconButtonRow
    ) {
        boolean showAriesButton = AriesFeatures.ARIES_PAUSE_MENU_BUTTON.isEnabled();
        boolean onHypixelNetwork = HypixelState.isOnHypixelNetwork()
            && AriesFeatures.HYPIXEL_PAUSE_MENU_BUTTON.isEnabled();

        if (!showAriesButton && !onHypixelNetwork) {
            return;
        }

        if (onHypixelNetwork) {
            Minecraft minecraft = Minecraft.getInstance();

            SpriteIconButton hypixelButton = SpriteIconButton.builder(
                    Component.literal("Hypixel"),
                    _ -> {
                        if (minecraft.player == null) {
                            return;
                        }

                        minecraft.player.connection.registryAccess()
                            .lookupOrThrow(Registries.DIALOG)
                            .get(Dialogs.SERVER_LINKS)
                            .ifPresent(dialog ->
                                minecraft.player.connection.showDialog(dialog, (PauseScreen) (Object) this)
                            );
                    },
                    true
                )
                .width(BUTTON_SIZE).sprite(Aries.id("hypixel_logo"), SPRITE_SIZE, SPRITE_SIZE).withTootip().build();

            iconButtonRow.addChild(hypixelButton);
        }

        if (showAriesButton) {
            SpriteIconButton ariesButton = SpriteIconButton.builder(
                    Component.translatable("aries.mod_name"),
                    _ -> Minecraft.getInstance().gui.setScreen(new AriesScreen()),
                    true
                )
                .width(BUTTON_SIZE)
                .sprite(Aries.id("logo_icon"), SPRITE_SIZE, SPRITE_SIZE)
                .withTootip()
                .build();

            iconButtonRow.addChild(ariesButton);
        }
    }
    //?}



    //? if 26.1.2 {
    /*@Inject(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0))
    private void ariesPauseMenuButton(CallbackInfo ci, @Local(name = "helper") GridLayout.RowHelper helper) {
        boolean showAriesButton = AriesFeatures.ARIES_PAUSE_MENU_BUTTON.isEnabled();
        boolean onHypixelNetwork = HypixelState.isOnHypixelNetwork()
            && AriesFeatures.HYPIXEL_PAUSE_MENU_BUTTON.isEnabled();

        if (!showAriesButton && !onHypixelNetwork) {
            return;
        }

        LinearLayout buttonRow = LinearLayout.horizontal().spacing(PauseScreen.BUTTON_PADDING * 2);

        if (onHypixelNetwork) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player != null) {
                ServerLinks serverLinks = minecraft.player.connection.serverLinks();

                if (!serverLinks.isEmpty()) {
                    Button hypixelButton = Button.builder(
                            Component.literal("Hypixel"),
                            _ -> {
                                if (minecraft.player == null) {
                                    return;
                                }

                                minecraft.player.connection.registryAccess()
                                    .lookupOrThrow(Registries.DIALOG)
                                    .get(Dialogs.SERVER_LINKS)
                                    .ifPresent(
                                        dialog ->
                                            minecraft.player.connection.showDialog(dialog, (PauseScreen) (Object) this)
                                    );
                            }).width(PauseScreen.BUTTON_WIDTH_HALF).build();

                    buttonRow.addChild(hypixelButton);
                }
            }
        }

        if (showAriesButton) {
            int buttonWidth = AriesFeatures.HYPIXEL_PAUSE_MENU_BUTTON.isEnabled()
                ? PauseScreen.BUTTON_WIDTH_HALF : PauseScreen.BUTTON_WIDTH_FULL;

            Button ariesButton = Button.builder(
                Component.translatable("aries.mod_name"),
                _ ->
                    Minecraft.getInstance().setScreen(new AriesScreen())).width(buttonWidth).build();
            buttonRow.addChild(ariesButton);
        }

        helper.addChild(buttonRow, 2);

    }
    *///?}

}

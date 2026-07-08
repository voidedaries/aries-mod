package dev.voidedaries.aries.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.gui.AriesScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AriesCommands {
    static int SINGLE_FAIL = 1;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root =
            ClientCommands.literal("aries").executes(
                _ -> openAriesMenu()
            );

        root.then(ClientCommands.literal("config")
            .then(ClientCommands.literal("save")
                .executes(_ -> {
                    AriesConfig.save();
                    Aries.log("Saved Aries config");
                    return SINGLE_FAIL;
                })
            ));

        root.then(ClientCommands.literal("config")
            .then(ClientCommands.literal("reset")
                .executes(_ -> {
                    AriesConfig.resetToDefaults();
                    Aries.log("Reset Aries config");
                    return Command.SINGLE_SUCCESS;
                })
            ));

        root.then(ClientCommands.literal("debug")
            .then(ClientCommands.literal("item")
                .then(ClientCommands.literal("data")
                .executes(_ -> debugItemData()))
            ));


        dispatcher.register(root);
    }

    private static int openAriesMenu() {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.setScreen(new AriesScreen()));

        if (Minecraft.getInstance().player == null) {
            return SINGLE_FAIL;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int debugItemData() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.level == null) {
            return SINGLE_FAIL;
        }

        ItemEntity closest = minecraft.level.getEntitiesOfClass(
            ItemEntity.class,
            player.getBoundingBox().inflate(5)
        ).stream().findFirst().orElse(null);

        if (closest == null) {
            Aries.log("No item entities nearby.");
            return SINGLE_FAIL;
        }

        ItemStack stack = closest.getItem();

        Aries.log("========== ITEM DEBUG ==========");

        Aries.log("Name: " + stack.getHoverName().getString());
        Aries.log("Item ID: " + BuiltInRegistries.ITEM.getKey(stack.getItem()));
        Aries.log("Count: " + stack.getCount());

        Aries.log("--- Components ---");
        stack.getComponents().forEach(c -> Aries.log(c.type() + " = " + c.value()));

        Aries.log("--- Stack ToString ---");
        Aries.log(stack.toString());

        Aries.log("--- Tooltip ---");
        List<Component> tooltip = stack.getTooltipLines(
            Item.TooltipContext.EMPTY,
            minecraft.player,
            TooltipFlag.NORMAL
        );

        for (Component line : tooltip) {
            Aries.log(line.getString());
        }

        Aries.log("========== END ==========");

        return Command.SINGLE_SUCCESS;
    }
}

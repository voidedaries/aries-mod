package dev.voidedaries.aries.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.voidedaries.aries.Aries;
import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.gui.AriesScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class AriesCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> root =
            ClientCommandManager.literal("aries").executes(
                _ -> openAriesMenu()
            );

        root.then(ClientCommandManager.literal("config")
            .then(ClientCommandManager.literal("save")
                .executes(_ -> {
                    AriesConfig.save();
                    Aries.log("Saved Aries config!");
                    return Command.SINGLE_SUCCESS;
                })
            ));

        dispatcher.register(root);
    }

    private static int openAriesMenu() {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new AriesScreen()));

        if (Minecraft.getInstance().player == null) {
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }
}

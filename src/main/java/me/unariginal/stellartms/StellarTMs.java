package me.unariginal.stellartms;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class StellarTMs implements ModInitializer {
    private final String MOD_ID = "stellartms";
    public final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static StellarTMs instance;
    public MinecraftServer server;
    public Config config;

    @Override
    public void onInitialize() {
        instance = this;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("stellartms")
                                    .then(
                                            CommandManager.literal("give")
                                                    .requires(Permissions.require("stellartms.give", 4))
                                                    .then(
                                                            CommandManager.literal("tm")
                                                                    .then(
                                                                            CommandManager.argument("move", StringArgumentType.string())
                                                                                    .executes(this::giveTM)
                                                                    )
                                                    )
                                                    .then(
                                                            CommandManager.literal("tr")
                                                                    .then(
                                                                            CommandManager.argument("move", StringArgumentType.string())
                                                                                    .executes(this::giveTR)
                                                                    )
                                                    )
                                    )
                            .then(
                                    CommandManager.literal("reload")
                                            .requires(Permissions.require("stellartms.reload", 4))
                                            .executes(ctx -> {
                                                config = new Config();
                                                ctx.getSource().sendMessage(Text.literal("[StellarTMs] Reloaded!"));
                                                return 1;
                                            })
                            )
            );
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.config = new Config();
        });
    }

    private int giveTR(CommandContext<ServerCommandSource> ctx) {
        String move = StringArgumentType.getString(ctx, "move");
        if (ctx.getSource().isExecutedByPlayer()) {
            Objects.requireNonNull(ctx.getSource().getPlayer()).giveItemStack(config.settings.getItem(move, "tr"));
            return 1;
        }
        return 0;
    }

    private int giveTM(CommandContext<ServerCommandSource> ctx) {
        String move = StringArgumentType.getString(ctx, "move");
        if (ctx.getSource().isExecutedByPlayer()) {
            Objects.requireNonNull(ctx.getSource().getPlayer()).giveItemStack(config.settings.getItem(move, "tm"));
            return 1;
        }
        return 0;
    }
}

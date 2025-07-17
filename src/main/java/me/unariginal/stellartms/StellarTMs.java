package me.unariginal.stellartms;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.mojang.brigadier.arguments.StringArgumentType;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.stellartms.data.DataComponents;
import me.unariginal.stellartms.handler.ItemHandler;
import me.unariginal.stellartms.items.TM;
import me.unariginal.stellartms.items.TR;
import me.unariginal.stellartms.utils.TextUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StellarTMs implements ModInitializer {
    public static final String MOD_ID = "stellartms";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static StellarTMs INSTANCE;
    public MinecraftServer server;
    public FabricServerAudiences audiences;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("stellartms")
                            .then(
                                    CommandManager.literal("give")
                                            .requires(Permissions.require("stellartms.give", 4))
                                            .then(
                                                    CommandManager.argument("player", EntityArgumentType.player())
                                                            .then(
                                                                    CommandManager.literal("tm")
                                                                            .then(
                                                                                    CommandManager.argument("move", StringArgumentType.string())
                                                                                            .suggests((ctx, builder) -> {
                                                                                                builder.suggest("blank");
                                                                                                Moves.INSTANCE.all().forEach(move -> builder.suggest(move.getName().toLowerCase()));
                                                                                                return builder.buildFuture();
                                                                                            })
                                                                                            .executes(ctx -> {
                                                                                                String move = StringArgumentType.getString(ctx, "move");
                                                                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                                                ItemStack tmToGive = ItemHandler.StellarTM.getDefaultStack();
                                                                                                tmToGive.applyComponentsFrom(ComponentMap.builder().add(DataComponents.MOVE, move).build());
                                                                                                player.giveItemStack(tmToGive);
                                                                                                return 1;
                                                                                            })
                                                                            )
                                                            )
                                            )
                                            .then(
                                                    CommandManager.argument("player", EntityArgumentType.player())
                                                            .then(
                                                                    CommandManager.literal("tr")
                                                                            .then(
                                                                                    CommandManager.argument("move", StringArgumentType.string())
                                                                                            .suggests((ctx, builder) -> {
                                                                                                builder.suggest("blank");
                                                                                                Moves.INSTANCE.all().forEach(move -> builder.suggest(move.getName()));
                                                                                                return builder.buildFuture();
                                                                                            })
                                                                                            .executes(ctx -> {
                                                                                                String move = StringArgumentType.getString(ctx, "move");
                                                                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                                                ItemStack tmToGive = ItemHandler.StellarTR.getDefaultStack();
                                                                                                tmToGive.applyComponentsFrom(ComponentMap.builder().add(DataComponents.MOVE, move).build());
                                                                                                player.giveItemStack(tmToGive);
                                                                                                return 1;
                                                                                            })
                                                                            )
                                                            )
                                            )
                            )
            );
        });

        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        ItemHandler.registerItems();
        ItemHandler.registerItemGroup();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.audiences = FabricServerAudiences.of(server);
        });
    }
}

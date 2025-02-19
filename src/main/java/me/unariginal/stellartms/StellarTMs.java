package me.unariginal.stellartms;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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

    @Override
    public void onInitialize() {
        instance = this;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("stellartms")
                                    .then(
                                            CommandManager.literal("give")
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
            );
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.server = server);
    }

    private int giveTR(CommandContext<ServerCommandSource> ctx) {
        ItemStack tm_to_give = new ItemStack(Items.NETHER_BRICK);
        String move = StringArgumentType.getString(ctx, "move");

        return giveItem(tm_to_give, move, "tr", ctx.getSource().getPlayer());
    }

    private int giveTM(CommandContext<ServerCommandSource> ctx) {
        ItemStack tm_to_give = new ItemStack(Items.BRICK);
        String move = StringArgumentType.getString(ctx, "move");
        return giveItem(tm_to_give, move, "tm", ctx.getSource().getPlayer());
    }

    private int giveItem(ItemStack item, String move, String type, ServerPlayerEntity player) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("stellar_item_type", type);
        nbtCompound.putString("stellar_move", move);

        int model_data = 10000;
        if (!move.equalsIgnoreCase("blank")) {
            switch (Objects.requireNonNull(Moves.INSTANCE.getByName(move)).create().getType().getName()) {
                case "normal":
                    model_data = 10001;
                    break;
                case "fire":
                    model_data = 10002;
                    break;
                case "water":
                    model_data = 10003;
                    break;
                case "electric":
                    model_data = 10004;
                    break;
                case "grass":
                    model_data = 10005;
                    break;
                case "ice":
                    model_data = 10006;
                    break;
                case "fighting":
                    model_data = 10007;
                    break;
                case "poison":
                    model_data = 10008;
                    break;
                case "ground":
                    model_data = 10009;
                    break;
                case "flying":
                    model_data = 10010;
                    break;
                case "psychic":
                    model_data = 10011;
                    break;
                case "bug":
                    model_data = 10012;
                    break;
                case "rock":
                    model_data = 10013;
                    break;
                case "ghost":
                    model_data = 10014;
                    break;
                case "dragon":
                    model_data = 10015;
                    break;
                case "dark":
                    model_data = 10016;
                    break;
                case "steel":
                    model_data = 10017;
                    break;
                case "fairy":
                    model_data = 10018;
                    break;
                default:
                    return 0;
            }
        }
        item.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(model_data)).add(DataComponentTypes.ITEM_NAME, (!move.equalsIgnoreCase("blank")) ? (Objects.requireNonNull(Moves.INSTANCE.getByName(move)).create().getDisplayName()) : Text.literal("Blank " + type.toUpperCase())).add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound)).build());

        player.giveItemStack(item);

        return 1;
    }
}

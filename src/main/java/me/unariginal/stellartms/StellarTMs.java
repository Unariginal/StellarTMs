package me.unariginal.stellartms;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import kotlin.random.Random;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.stellartms.config.Config;
import me.unariginal.stellartms.data.DataComponents;
import me.unariginal.stellartms.handler.ItemHandler;
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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StellarTMs implements ModInitializer {
    public static final String MOD_ID = "stellartms";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static StellarTMs INSTANCE;
    public static boolean DEBUG = false;

    public MinecraftServer server;
    public FabricServerAudiences audiences;
    public Config config;

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
                                                    CommandManager.argument("player", EntityArgumentType.players())
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
                                                                                                MoveTemplate moveTemplate = Moves.INSTANCE.getByName(move);
                                                                                                if (moveTemplate == null) {
                                                                                                    move = "blank";
                                                                                                }

                                                                                                List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player").stream().toList();
                                                                                                giveTM(ctx, moveTemplate, move, players);
                                                                                                return 1;
                                                                                            })
                                                                            )
                                                                            .then(
                                                                                    CommandManager.literal("random")
                                                                                            .executes(ctx -> {
                                                                                                List<MoveTemplate> validMoveTemplates = getValidMoves("");
                                                                                                MoveTemplate moveTemplate = validMoveTemplates.get(Random.Default.nextInt(validMoveTemplates.size()));
                                                                                                String move = moveTemplate.getName();

                                                                                                List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player").stream().toList();
                                                                                                giveTM(ctx, moveTemplate, move, players);
                                                                                                return 1;
                                                                                            })
                                                                                            .then(
                                                                                                    CommandManager.argument("filter", StringArgumentType.string())
                                                                                                            .executes(ctx -> {
                                                                                                                String filterArg = StringArgumentType.getString(ctx, "filter");
                                                                                                                List<MoveTemplate> validMoveTemplates = getValidMoves(filterArg);
                                                                                                                MoveTemplate randomSelection = validMoveTemplates.get(Random.Default.nextInt(validMoveTemplates.size()));

                                                                                                                List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player").stream().toList();
                                                                                                                giveTM(ctx, randomSelection, randomSelection.getName(), players);
                                                                                                                return 1;
                                                                                                            })
                                                                                            )
                                                                            )
                                                            )
                                            )
                                            .then(
                                                    CommandManager.argument("player", EntityArgumentType.players())
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
                                                                                                MoveTemplate moveTemplate = Moves.INSTANCE.getByName(move);
                                                                                                if (moveTemplate == null) {
                                                                                                    move = "blank";
                                                                                                }

                                                                                                List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player").stream().toList();
                                                                                                giveTR(ctx, moveTemplate, move, players);
                                                                                                return 1;
                                                                                            })
                                                                            )
                                                                            .then(
                                                                                    CommandManager.literal("random")
                                                                                            .executes(ctx -> {
                                                                                                List<MoveTemplate> validMoveTemplates = getValidMoves("");
                                                                                                MoveTemplate moveTemplate = validMoveTemplates.get(Random.Default.nextInt(validMoveTemplates.size()));
                                                                                                String move = moveTemplate.getName();

                                                                                                List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player").stream().toList();
                                                                                                giveTR(ctx, moveTemplate, move, players);
                                                                                                return 1;
                                                                                            })
                                                                                            .then(
                                                                                                    CommandManager.argument("filter", StringArgumentType.string())
                                                                                                            .executes(ctx -> {
                                                                                                                String filterArg = StringArgumentType.getString(ctx, "filter");
                                                                                                                List<MoveTemplate> validMoveTemplates = getValidMoves(filterArg);
                                                                                                                MoveTemplate randomSelection = validMoveTemplates.get(Random.Default.nextInt(validMoveTemplates.size()));

                                                                                                                List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "player").stream().toList();
                                                                                                                giveTR(ctx, randomSelection, randomSelection.getName(), players);
                                                                                                                return 1;
                                                                                                            })
                                                                                            )
                                                                            )
                                                            )
                                            )
                            )
                            .then(
                                    CommandManager.literal("reload")
                                            .requires(Permissions.require("stellartms.reload", 4))
                                            .executes(ctx -> {
                                                reload();
                                                ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(config.reload_message)));
                                                return 1;
                                            })
                            )
            );
        });

        reload();

        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        ItemHandler.registerItems();
        ItemHandler.registerItemGroup();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.audiences = FabricServerAudiences.of(server);
        });
    }

    public void reload() {
        this.config = new Config();
    }

    public void giveTM(CommandContext<ServerCommandSource> ctx, MoveTemplate moveTemplate, String move, List<ServerPlayerEntity> players) {
        ItemStack toGive = ItemHandler.StellarTM.getDefaultStack();
        toGive.applyComponentsFrom(ComponentMap.builder().add(DataComponents.MOVE, move).build());
        for (ServerPlayerEntity player : players) {
            player.giveItemStack(toGive);
            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(config.tm_give_message, player), moveTemplate)));
        }
    }

    public void giveTR(CommandContext<ServerCommandSource> ctx, MoveTemplate moveTemplate, String move, List<ServerPlayerEntity> players) {
        ItemStack toGive = ItemHandler.StellarTR.getDefaultStack();
        toGive.applyComponentsFrom(ComponentMap.builder().add(DataComponents.MOVE, move).build());
        for (ServerPlayerEntity player : players) {
            player.giveItemStack(toGive);
            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(config.tr_give_message, player), moveTemplate)));
        }
    }

    public List<MoveTemplate> getValidMoves(String filterArg) {
        String[] filters = filterArg.split(";");

        List<MoveTemplate> validMoveTemplates = new ArrayList<>(Moves.INSTANCE.all());

        boolean containsZFilter = false;
        boolean containsMaxFilter = false;
        boolean containsGmaxFilter = false;

        for (String filter : filters) {
            String[] filterDetails = filter.split(":");
            if (filter.length() < 2) continue;
            String filterType = filterDetails[0];
            String filterOption = filterDetails[1];

            boolean antiFilter = filterType.startsWith("!");
            if (antiFilter) filterType = filterType.substring(1);
            /*
             * Filter Types:
             * Type:
             *   - One of the 18 elemental types
             *   - type:normal,fire,water
             * Category:
             *   - Special, Physical, Status
             *   - category:special
             * Gimmick:
             *   - Z Moves, Max Moves, Gmax Moves
             *   - gimmick:!zpower
             * Learned By:
             *   - Sort by a species' learnset [tm, tutor, egg, levelup/level]
             *   - learnedby:pikachu#tm#tutor
             *
             * Full example:
             *   /stellartms give <player> tm random type:water,normal,fire;!category:status;learnedby:pikachu#tm
             * */

            String finalFilterOption = filterOption;
            switch (filterType) {
                case "type" -> {
                    String[] types = finalFilterOption.split(",");
                    List<String> validTypes = new ArrayList<>();
                    for (String type : types) {
                        if (ElementalTypes.INSTANCE.all().stream().anyMatch(elementalType -> elementalType.getName().equalsIgnoreCase(type))) {
                            validTypes.add(type);
                        }
                    }

                    validMoveTemplates.removeIf(template -> {
                        if (antiFilter) {
                            for (String type : validTypes) {
                                boolean typeMatches = template.getElementalType().getName().equalsIgnoreCase(type);
                                if (typeMatches) return true;
                            }
                            return false;
                        } else {
                            for (String type : validTypes) {
                                if (template.getElementalType().getName().equalsIgnoreCase(type)) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    });
                }
                case "category" -> {
                    if (finalFilterOption.equalsIgnoreCase("status")
                            || finalFilterOption.equalsIgnoreCase("physical")
                            || finalFilterOption.equalsIgnoreCase("special")
                    ) {
                        validMoveTemplates.removeIf(template -> {
                            boolean categoryMatches = template.getDamageCategory().getName().equalsIgnoreCase(finalFilterOption);
                            if (antiFilter) {
                                return categoryMatches;
                            } else {
                                return !categoryMatches;
                            }
                        });
                    }
                }
                case "gimmick" -> {
                    if (finalFilterOption.equalsIgnoreCase("z")
                            || finalFilterOption.equalsIgnoreCase("zmove")
                            || finalFilterOption.equalsIgnoreCase("zpower")
                    ) {
                        containsZFilter = true;
                        validMoveTemplates.removeIf(template -> {
                            boolean isZMove = config.zMoves.contains(template.getName().toLowerCase());

                            if (antiFilter) {
                                return isZMove;
                            } else {
                                return !isZMove;
                            }
                        });
                    }

                    if (finalFilterOption.equalsIgnoreCase("max")
                            || finalFilterOption.equalsIgnoreCase("dynamax")
                    ) {
                        containsMaxFilter = true;
                        validMoveTemplates.removeIf(template -> {
                            boolean isMaxMove = config.maxMoves.contains(template.getName().toLowerCase());

                            if (antiFilter) {
                                return isMaxMove;
                            } else {
                                return !isMaxMove;
                            }
                        });
                    }

                    if (finalFilterOption.equalsIgnoreCase("gmax")
                            || finalFilterOption.equalsIgnoreCase("gigantamax")
                    ) {
                        containsGmaxFilter = true;
                        validMoveTemplates.removeIf(template -> {
                            boolean isGMaxMove = config.gmaxMoves.contains(template.getName().toLowerCase());

                            if (antiFilter) {
                                return isGMaxMove;
                            } else {
                                return !isGMaxMove;
                            }
                        });
                    }
                }
                case "learnedby", "learnset" -> {
                    String[] split = finalFilterOption.split("#");
                    boolean containsTM = false;
                    boolean containsEgg = false;
                    boolean containsTutor = false;
                    boolean containsLevel = false;

                    for (String option : split) {
                        if (option.equalsIgnoreCase("tm")) containsTM = true;
                        if (option.equalsIgnoreCase("egg")) containsEgg = true;
                        if (option.equalsIgnoreCase("tutor")) containsTutor = true;
                        if (option.equalsIgnoreCase("level") || option.equalsIgnoreCase("levelup"))
                            containsLevel = true;
                    }

                    Species species = PokemonSpecies.INSTANCE.getByName(split[0]);
                    if (species != null) {
                        List<MoveTemplate> validLearnsets = new ArrayList<>();
                        // If nothing, get everything :D
                        if (!containsTutor && !containsTM && !containsEgg && !containsLevel) {
                            validLearnsets.addAll(species.getMoves().getTmMoves());
                            validLearnsets.addAll(species.getMoves().getEggMoves());
                            validLearnsets.addAll(species.getMoves().getTutorMoves());
                            for (List<MoveTemplate> levelUpMoves : species.getMoves().getLevelUpMoves().values()) {
                                validLearnsets.addAll(levelUpMoves);
                            }
                        }

                        if (containsLevel) {
                            for (List<MoveTemplate> levelUpMoves : species.getMoves().getLevelUpMoves().values()) {
                                validLearnsets.addAll(levelUpMoves);
                            }
                        }
                        if (containsTM) validLearnsets.addAll(species.getMoves().getTmMoves());
                        if (containsEgg) validLearnsets.addAll(species.getMoves().getEggMoves());
                        if (containsTutor) validLearnsets.addAll(species.getMoves().getTutorMoves());

                        validMoveTemplates.removeIf(moveTemplate -> !validLearnsets.contains(moveTemplate));
                    }
                }
            }

            // Remove gimmick moves if we need to
            boolean finalContainsZFilter = containsZFilter;
            boolean finalContainsMaxFilter = containsMaxFilter;
            boolean finalContainsGmaxFilter = containsGmaxFilter;
            validMoveTemplates.removeIf(template -> {
                boolean isZMove = config.zMoves.contains(template.getName().toLowerCase());
                boolean isMaxMove = config.maxMoves.contains(template.getName().toLowerCase());
                boolean isGMaxMove = config.gmaxMoves.contains(template.getName().toLowerCase());

                if (!finalContainsZFilter && config.autoExcludeZMoves && isZMove) {
                    return true;
                }

                if (!finalContainsMaxFilter && config.autoExcludeMaxMoves && isMaxMove) {
                    return true;
                }

                if (!finalContainsGmaxFilter && config.autoExcludeGMaxMoves && isGMaxMove) {
                    return true;
                }

                return false;
            });
        }

        return validMoveTemplates;
    }
}
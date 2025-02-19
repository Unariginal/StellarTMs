package me.unariginal.stellartms.mixin;

import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.unariginal.stellartms.StellarTMs;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public abstract class InteractPokemonEntityMixin /*extends TameableShoulderEntity implements PosableEntity, Shearable, Schedulable, ScannableEntity*/ {
    @Inject(method = "interactMob", at = @At("HEAD"))
    private void injected(PlayerEntity playerEntity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
        StellarTMs.instance.LOGGER.info("Interaction Detected");
        if (!pokemonEntity.isBattling() && pokemonEntity.isBattleClone()) {
            StellarTMs.instance.LOGGER.info("Battling Detected, Cancelling injection");
            return;
        }
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (pokemonEntity.getOwnerUuid() == playerEntity.getUuid()) {
            StellarTMs.instance.LOGGER.info("Player is the owner");
            if (itemStack.getItem() == Items.BRICK || itemStack.getItem() == Items.NETHER_BRICK) {
                StellarTMs.instance.LOGGER.info("Brick Detected");
                if (itemStack.contains(DataComponentTypes.CUSTOM_DATA) && itemStack.get(DataComponentTypes.CUSTOM_DATA) != null) {
                    NbtCompound nbt = itemStack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
                    if (nbt.contains("stellar_item_type") && nbt.getString("stellar_item_type") != null) {
                        StellarTMs.instance.LOGGER.info("Stellar Detected");
                        boolean isTR = (nbt.getString("stellar_item_type").equalsIgnoreCase("tr"));
                        if ((nbt.contains("stellar_move") && nbt.getString("stellar_move") != null)) {
                            String move_str = nbt.getString("stellar_move");
                            Pokemon pokemon = pokemonEntity.getPokemon();
                            if (!move_str.equalsIgnoreCase("blank")) {
                                StellarTMs.instance.LOGGER.info("Not blank");
                                MoveTemplate move = Moves.INSTANCE.getByName(move_str);
                                if (move != null) {
                                    StellarTMs.instance.LOGGER.info("Move Detected");
                                    if (pokemon.getSpecies().getMoves().getTmMoves().contains(move)) {
                                        StellarTMs.instance.LOGGER.info("Move can be learned");
                                        if (pokemon.getMoveSet().hasSpace()) {
                                            pokemon.getMoveSet().add(move.create());
                                        } else {
                                            pokemon.getBenchedMoves().add(new BenchedMove(move, 0));
                                        }

                                        if (!playerEntity.isCreative() && isTR) {
                                            itemStack.decrement(1);
                                        }
                                    }
                                } else {
                                    StellarTMs.instance.LOGGER.info("Uh oh, no move");
                                }
                            } else {
                                ServerPlayerEntity player = StellarTMs.instance.server.getPlayerManager().getPlayer(playerEntity.getUuid());
                                if (player != null) {
                                    SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
                                    gui.setTitle(Text.literal("Pick a move!"));

                                    int slot = 2;
                                    for (Move move : pokemon.getMoveSet().getMoves()) {
                                        ItemStack display_tm = new ItemStack(Items.BRICK);
                                        ElementalType type = move.getType();

                                        int model_data;
                                        switch (type.getName()) {
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
                                                return;
                                        }
                                        display_tm.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(model_data)).add(DataComponentTypes.ITEM_NAME, move.getDisplayName()).build());
                                        GuiElement element = new GuiElementBuilder(display_tm).setCallback((i, clickType, slotActionType) -> {
                                            ItemStack tm_to_give;
                                            NbtCompound nbtCompound = new NbtCompound();
                                            if (isTR) {
                                                tm_to_give = new ItemStack(Items.NETHER_BRICK);
                                                nbtCompound.putString("stellar_item_type", "tr");
                                            } else {
                                                tm_to_give = new ItemStack(Items.BRICK);
                                                nbtCompound.putString("stellar_item_type", "tm");
                                            }
                                            nbtCompound.putString("stellar_move", move.getName());
                                            tm_to_give.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(model_data)).add(DataComponentTypes.ITEM_NAME, move.getDisplayName()).add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound)).build());

                                            if (!playerEntity.isCreative()) {
                                                itemStack.decrement(1);
                                            }
                                            player.giveItemStack(tm_to_give);

                                            gui.close();
                                        }).build();
                                        gui.setSlot(slot, element);
                                        slot++;
                                        if (slot == 4) {
                                            slot++;
                                        }
                                    }
                                    gui.open();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package me.unariginal.stellartms.mixin;

import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.unariginal.stellartms.StellarTMs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.component.DataComponentTypes;
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

import java.awt.*;
import java.util.Objects;

@Mixin(PokemonEntity.class)
public abstract class InteractPokemonEntityMixin {
    @Inject(method = "interactMob", at = @At("HEAD"))
    private void injected(PlayerEntity playerEntity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
        ServerPlayerEntity player = StellarTMs.instance.server.getPlayerManager().getPlayer(playerEntity.getUuid());
        if (player != null) {
            if (!pokemonEntity.isBattling() && pokemonEntity.isBattleClone()) {
                return;
            }

            ItemStack itemStack = playerEntity.getStackInHand(hand);
            if (pokemonEntity.getOwnerUuid() == playerEntity.getUuid()) {
                if (itemStack.getItem() == Items.BRICK || itemStack.getItem() == Items.NETHER_BRICK) {
                    if (itemStack.contains(DataComponentTypes.CUSTOM_DATA) && itemStack.get(DataComponentTypes.CUSTOM_DATA) != null) {
                        NbtCompound nbt = Objects.requireNonNull(itemStack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
                        if (nbt.contains("stellar_item_type") && nbt.getString("stellar_item_type") != null) {
                            boolean isTR = (nbt.getString("stellar_item_type").equalsIgnoreCase("tr"));
                            if ((nbt.contains("stellar_move") && nbt.getString("stellar_move") != null)) {
                                String move_str = nbt.getString("stellar_move");
                                Pokemon pokemon = pokemonEntity.getPokemon();
                                if (!move_str.equalsIgnoreCase("blank")) {
                                    MoveTemplate move = Moves.INSTANCE.getByName(move_str);
                                    if (move != null) {
                                        if (pokemon.getSpecies().getMoves().getTmMoves().contains(move) && !pokemon.getAllAccessibleMoves().contains(move)) {
                                            if (pokemon.getMoveSet().hasSpace()) {
                                                pokemon.getMoveSet().add(move.create());
                                            } else {
                                                pokemon.getBenchedMoves().add(new BenchedMove(move, 0));
                                            }

                                            if (!playerEntity.isCreative() && isTR) {
                                                itemStack.decrement(1);
                                            }

                                            player.sendActionBar(Component.text()
                                                    .color(TextColor.color(0, 255, 0))
                                                    .append(pokemon.getDisplayName())
                                                    .append(Component.text(" has learned "))
                                                    .append(move.getDisplayName())
                                                    .append(Component.text("!"))
                                                    .build());
                                        }
                                    }
                                } else {
                                    SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X1, player, false);
                                    gui.setTitle(Text.literal(StellarTMs.instance.config.settings.gui_title()));

                                    int slot = 2;
                                    for (Move move : pokemon.getMoveSet().getMoves()) {
                                        ItemStack display_tm = StellarTMs.instance.config.settings.getItem(move.getName(), (isTR) ? "tr" : "tm");
                                        GuiElement element = new GuiElementBuilder(display_tm).setCallback((i, clickType, slotActionType) -> {
                                            if (!playerEntity.isCreative()) {
                                                itemStack.decrement(1);
                                            }
                                            player.giveItemStack(display_tm);

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

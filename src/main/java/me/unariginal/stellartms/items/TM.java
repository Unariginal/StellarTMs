package me.unariginal.stellartms.items;

import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.unariginal.stellartms.StellarTMs;
import me.unariginal.stellartms.data.DataComponents;
import me.unariginal.stellartms.handler.ItemHandler;
import me.unariginal.stellartms.utils.NbtUtils;
import me.unariginal.stellartms.utils.TextUtils;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TM extends SimplePolymerItem {
    private final Map<String, PolymerModelData> typeModelData = new HashMap<>();

    public TM(Item.Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        typeModelData.put("blank", PolymerResourcePackUtils.requestModel(polymerItem, Identifier.of(StellarTMs.MOD_ID, "item/tms/blank")));
        for (ElementalType type : ElementalTypes.INSTANCE.all()) {
            typeModelData.put(type.getName().toLowerCase(), PolymerResourcePackUtils.requestModel(polymerItem, Identifier.of(StellarTMs.MOD_ID, "item/tms/" + type.getName().toLowerCase())));
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (itemStack.getComponents().contains(DataComponents.MOVE)) {
            String moveComponent = itemStack.getComponents().get(DataComponents.MOVE);
            if (moveComponent != null) {
                MoveTemplate moveTemplate = Moves.INSTANCE.getByName(moveComponent);
                if (moveComponent.equalsIgnoreCase("blank") || moveTemplate == null) {
                    NbtUtils.setItemName(itemStack, "<gray>Blank TM");
                } else {
                    NbtUtils.setItemName(itemStack, getColorByType(moveTemplate) + "TM: " + moveTemplate.getDisplayName().getString());
                }
            }
        }
        return super.getPolymerItem(itemStack, player);
    }

    private String getType(MoveTemplate moveTemplate) {
        if (moveTemplate == null) return "blank";
        return moveTemplate.getElementalType().getName().toLowerCase();
    }

    public String getColorByType(MoveTemplate moveTemplate) {
        if (moveTemplate != null) {
            String type = String.valueOf(moveTemplate.getElementalType().getHue());
            type = type.replaceAll("0x", "");
            return "<#" + type.toLowerCase() + ">";
        }
        return "";
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (itemStack.getComponents().contains(DataComponents.MOVE)) {
            String moveComponent = itemStack.getComponents().get(DataComponents.MOVE);
            if (moveComponent != null) {
                MoveTemplate moveTemplate = Moves.INSTANCE.getByName(moveComponent);
                return typeModelData.get(getType(moveTemplate)).value();
            }
        }
        return typeModelData.get("blank").value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack itemStack, PlayerEntity user, LivingEntity livingEntity, Hand hand) {
        if (livingEntity instanceof PokemonEntity pokemonEntity) {
            Pokemon pokemon = pokemonEntity.getPokemon();
            if (pokemon.isPlayerOwned()) {
                ServerPlayerEntity player = pokemon.getOwnerPlayer();
                if (player != null) {
                    if (player.getUuid().equals(user.getUuid())) {
                        if (!pokemonEntity.isBattling()) {
                            if (itemStack.getComponents().contains(DataComponents.MOVE)) {
                                String moveComponent = itemStack.getComponents().get(DataComponents.MOVE);
                                if (moveComponent != null) {
                                    MoveTemplate moveTemplate = Moves.INSTANCE.getByName(moveComponent);
                                    if (moveTemplate == null) {
                                        SimpleGui gui = new SimpleGui(ScreenHandlerType.HOPPER, player, false);
                                        gui.setTitle(Text.literal("Pick A Move!"));

                                        int slot = 0;
                                        for (Move move : pokemon.getMoveSet().getMoves()) {
                                            ItemStack item = ItemHandler.StellarTM.getDefaultStack();
                                            item.applyComponentsFrom(ComponentMap.builder().add(DataComponents.MOVE, move.getTemplate().getName().toLowerCase()).build());

                                            GuiElement element = new GuiElementBuilder(item).setCallback((i, clickType, slotActionType) -> {
                                                player.getStackInHand(hand).decrement(1);
                                                player.giveItemStack(item);

                                                gui.close();
                                            }).build();

                                            gui.setSlot(slot, element);
                                            slot++;

                                            if (slot == 2) {
                                                slot++;
                                            }
                                        }
                                        gui.open();
                                    } else {
                                        if (pokemon.getForm().getMoves().getTmMoves().contains(moveTemplate) || pokemon.getSpecies().getMoves().getTmMoves().contains(moveTemplate)) {
                                            if (pokemon.getMoveSet().hasSpace()) {
                                                pokemon.getMoveSet().add(moveTemplate.create());
                                            } else {
                                                pokemon.getBenchedMoves().add(new BenchedMove(moveTemplate, 0));
                                            }

                                            player.sendMessage(TextUtils.deserialize("<green>" + pokemon.getDisplayName().getString() + " has learned " + getColorByType(moveTemplate) + moveTemplate.getDisplayName().getString() + "!"), true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}

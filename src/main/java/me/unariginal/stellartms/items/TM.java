package me.unariginal.stellartms.items;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.item.battle.BagItem;
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
import me.unariginal.stellartms.utils.TextUtils;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TM extends SimplePolymerItem implements PokemonSelectingItem {
    private final Map<String, PolymerModelData> typeModelData = new HashMap<>();

    public TM(Item.Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        typeModelData.put("blank", PolymerResourcePackUtils.requestModel(polymerItem, Identifier.of(StellarTMs.MOD_ID, "item/tms/blank")));
        for (ElementalType type : ElementalTypes.all()) {
            typeModelData.put(type.getName().toLowerCase(), PolymerResourcePackUtils.requestModel(polymerItem, Identifier.of(StellarTMs.MOD_ID, "item/tms/" + type.getName().toLowerCase())));
        }
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack original, TooltipType tooltipType, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity viewer) {
        ItemStack out = super.getPolymerItemStack(original, tooltipType, lookup, viewer);
        String moveName = original.getComponents().get(DataComponents.MOVE);
        MoveTemplate mt = (moveName != null) ? Moves.getByName(moveName) : null;
        out.set(DataComponentTypes.CUSTOM_NAME, buildDisplayName(mt));

        return out;
    }

    private static Text buildDisplayName(@Nullable MoveTemplate mt) {
        if (mt == null) {
            return TextUtils.deserialize(StellarTMs.INSTANCE.config.blank_tm_item_name);
        }
        return TextUtils.deserialize(TextUtils.parse(StellarTMs.INSTANCE.config.tm_item_name, mt));
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        String moveName = itemStack.getComponents().get(DataComponents.MOVE);
        MoveTemplate mt = (moveName != null) ? Moves.getByName(moveName) : null;
        String key = (mt == null) ? "blank" : mt.getElementalType().getName().toLowerCase();
        return typeModelData.get(key).value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        String moveName = stack.getComponents().get(DataComponents.MOVE);
        MoveTemplate move = (moveName != null) ? Moves.getByName(moveName) : null;
        List<String> loreTemplate = (move != null) ? StellarTMs.INSTANCE.config.tm_item_lore : StellarTMs.INSTANCE.config.blank_tm_item_lore;

        for (String line : loreTemplate) {
            tooltip.add(TextUtils.deserialize(TextUtils.parse(line, move)));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            return this.use(player, player.getStackInHand(hand));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> use(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack) {
        return PokemonSelectingItem.super.use(serverPlayerEntity, itemStack);
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!this.canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (pokemon.isPlayerOwned()) {
            ServerPlayerEntity player = pokemon.getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(serverPlayerEntity.getUuid())) {
                    if (itemStack.getComponents().contains(DataComponents.MOVE)) {
                        String moveComponent = itemStack.getComponents().get(DataComponents.MOVE);
                        if (moveComponent != null) {
                            MoveTemplate moveTemplate = Moves.getByName(moveComponent);
                            if (moveTemplate == null) {
                                SimpleGui gui = new SimpleGui(ScreenHandlerType.HOPPER, player, false);
                                gui.setTitle(TextUtils.deserialize(TextUtils.parse(StellarTMs.INSTANCE.config.gui_title)));

                                int slot = 0;
                                for (Move move : pokemon.getMoveSet().getMoves()) {
                                    ItemStack item = ItemHandler.StellarTM.getDefaultStack();
                                    item.applyComponentsFrom(ComponentMap.builder().add(DataComponents.MOVE, move.getTemplate().getName().toLowerCase()).build());

                                    GuiElement element = new GuiElementBuilder(item).setCallback((i, clickType, slotActionType) -> {
                                        itemStack.decrement(1);
                                        player.giveItemStack(item);

                                        gui.close();
                                    }).build();

                                    gui.setSlot(slot, element);
                                    slot++;

                                    if (slot == 2) {
                                        gui.setSlot(slot, new GuiElementBuilder(Items.BARRIER).setName(TextUtils.deserialize("<red>Cancel")).setCallback((i, clickType, slotActionType) -> gui.close()).build());
                                        slot++;
                                    }
                                }
                                gui.open();
                            } else {
                                if (pokemon.getForm().getMoves().getTmMoves().contains(moveTemplate) || pokemon.getSpecies().getMoves().getTmMoves().contains(moveTemplate) || StellarTMs.INSTANCE.config.ignore_learnset) {
                                    for (Move move : pokemon.getMoveSet().getMoves()) {
                                        MoveTemplate template = move.getTemplate();
                                        if (template.getName().equalsIgnoreCase(moveTemplate.getName())) {
                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(StellarTMs.INSTANCE.config.already_learned_overlay, moveTemplate), pokemon)), true);
                                            return TypedActionResult.success(itemStack);
                                        }
                                    }

                                    for (BenchedMove move : pokemon.getBenchedMoves()) {
                                        MoveTemplate template = move.getMoveTemplate();
                                        if (template.getName().equalsIgnoreCase(moveTemplate.getName())) {
                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(StellarTMs.INSTANCE.config.already_learned_overlay, moveTemplate), pokemon)), true);
                                            return TypedActionResult.success(itemStack);
                                        }
                                    }

                                    if (pokemon.getMoveSet().hasSpace()) {
                                        pokemon.getMoveSet().add(moveTemplate.create());
                                    } else {
                                        pokemon.getBenchedMoves().add(new BenchedMove(moveTemplate, 0));
                                    }

                                    player.sendMessage(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(StellarTMs.INSTANCE.config.move_learned_overlay, moveTemplate), pokemon)), true);
                                } else {
                                    player.sendMessage(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(StellarTMs.INSTANCE.config.unable_to_learn_overlay, moveTemplate), pokemon)), true);
                                }
                            }
                        }
                    }
                }
            }
        }
        return TypedActionResult.success(itemStack);
    }

    @Override
    public void applyToBattlePokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull BattlePokemon battlePokemon) {

    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        return true;
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactWithSpecificBattle(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull BattlePokemon battlePokemon) {
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactGeneral(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack) {
        return PokemonSelectingItem.super.interactGeneral(serverPlayerEntity, itemStack);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> interactGeneralBattle(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull BattleActor battleActor) {
        return TypedActionResult.fail(itemStack);
    }
}

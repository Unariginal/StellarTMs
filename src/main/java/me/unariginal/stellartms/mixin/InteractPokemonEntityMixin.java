package me.unariginal.stellartms.mixin;

import com.bedrockk.molang.runtime.struct.QueryStruct;
import com.cobblemon.mod.common.api.entity.EntitySideDelegate;
import com.cobblemon.mod.common.api.moves.BenchedMove;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.scheduling.Schedulable;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.api.scheduling.SchedulingTracker;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.PosableEntity;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokedex.scanner.PokedexEntityData;
import com.cobblemon.mod.common.pokedex.scanner.ScannableEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import me.unariginal.stellartms.StellarTMs;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(PokemonEntity.class)
public abstract class InteractPokemonEntityMixin extends TameableShoulderEntity implements PosableEntity, Shearable, Schedulable, ScannableEntity {
    protected InteractPokemonEntityMixin(EntityType<? extends TameableShoulderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"))
    private void injected(PlayerEntity playerEntity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
        if (!pokemonEntity.isBattling() && pokemonEntity.isBattleClone()) {
            return;
        }
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (pokemonEntity.getOwnerUuid() == playerEntity.getUuid()) {
            if (itemStack.getItem() == Items.BRICK || itemStack.getItem() == Items.NETHER_BRICK) {
                if (itemStack.contains(DataComponentTypes.CUSTOM_DATA) && itemStack.get(DataComponentTypes.CUSTOM_DATA) != null) {
                    NbtCompound nbt = itemStack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
                    if (nbt.contains("stellar_item_type") && nbt.getString("stellar_item_type") != null) {
                        boolean isTR = (nbt.getString("stellar_item_type").equalsIgnoreCase("tr"));
                        if ((nbt.contains("stellar_move") && nbt.getString("stellar_move") != null)) {
                            String move_str = nbt.getString("stellar_move");
                            Pokemon pokemon = pokemonEntity.getPokemon();
                            if (!move_str.equalsIgnoreCase("blank")) {
                                MoveTemplate move = Moves.INSTANCE.getByName(move_str);
                                if (move != null) {
                                    ElementalType type = move.getElementalType();
                                    if (pokemon.getSpecies().getMoves().getTmMoves().contains(move)) {
                                        if (pokemon.getMoveSet().hasSpace()) {
                                            pokemon.getMoveSet().add(move.create());
                                        } else {
                                            pokemon.getBenchedMoves().add(new BenchedMove(move, 0));
                                        }

                                        if (!playerEntity.isCreative() && isTR) {
                                            itemStack.decrement(1);
                                        }
                                    }
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
                                            nbtCompound.putString("stellar_move", move.toString());
                                            tm_to_give.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound)).build());

                                            if (!playerEntity.isCreative()) {
                                                itemStack.decrement(1);
                                            }

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

    @Override
    public @NotNull SchedulingTracker getSchedulingTracker() {
        return null;
    }

    @Override
    public @NotNull ScheduledTask momentarily(@NotNull Function0<Unit> function0) {
        return null;
    }

    @Override
    public @NotNull ScheduledTask after(float v, @NotNull Function0<Unit> function0) {
        return null;
    }

    @Override
    public @NotNull ScheduledTask lerp(float v, @NotNull Function1<? super Float, Unit> function1) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<Unit> delayedFuture(float v) {
        return null;
    }

    @Override
    public @NotNull ScheduledTask.Builder taskBuilder() {
        return null;
    }

    @Override
    public @NotNull PoseType getCurrentPoseType() {
        return null;
    }

    @Override
    public @NotNull EntitySideDelegate<?> getDelegate() {
        return null;
    }

    @Override
    public @NotNull QueryStruct getStruct() {
        return null;
    }

    @Override
    public void addPosableFunctions(@NotNull QueryStruct queryStruct) {

    }

    @Override
    public @Nullable PokedexEntityData resolvePokemonScan() {
        return null;
    }

    @Override
    public @NotNull LivingEntity resolveEntityScan() {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public void sheared(SoundCategory shearedSoundCategory) {

    }

    @Override
    public boolean isShearable() {
        return false;
    }
}

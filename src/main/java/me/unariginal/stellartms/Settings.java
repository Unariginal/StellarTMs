package me.unariginal.stellartms;

import com.cobblemon.mod.common.api.moves.Moves;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record Settings(String tm_item,
                       int blank_tm,
                       int normal_tm,
                       int fire_tm,
                       int water_tm,
                       int electric_tm,
                       int grass_tm,
                       int ice_tm,
                       int fighting_tm,
                       int poison_tm,
                       int ground_tm,
                       int flying_tm,
                       int psychic_tm,
                       int bug_tm,
                       int rock_tm,
                       int ghost_tm,
                       int dragon_tm,
                       int dark_tm,
                       int steel_tm,
                       int fairy_tm,
                       String tr_item,
                       int blank_tr,
                       int normal_tr,
                       int fire_tr,
                       int water_tr,
                       int electric_tr,
                       int grass_tr,
                       int ice_tr,
                       int fighting_tr,
                       int poison_tr,
                       int ground_tr,
                       int flying_tr,
                       int psychic_tr,
                       int bug_tr,
                       int rock_tr,
                       int ghost_tr,
                       int dragon_tr,
                       int dark_tr,
                       int steel_tr,
                       int fairy_tr,
                       String gui_title) {
    public ItemStack getItem(String move, String type) {
        ItemStack item;
        boolean isTM = type.equalsIgnoreCase("tm");
        if (isTM) {
            item = new ItemStack(Registries.ITEM.get(Identifier.of(tm_item)));
        } else {
            item = new ItemStack(Registries.ITEM.get(Identifier.of(tr_item)));
        }

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("stellar_item_type", type);
        nbtCompound.putString("stellar_move", move);

        int model_data = (isTM) ? blank_tm : blank_tr;
        if (!move.equalsIgnoreCase("blank")) {
            switch (Objects.requireNonNull(Moves.INSTANCE.getByName(move)).create().getType().getName()) {
                case "normal":
                    model_data = (isTM) ? normal_tm : normal_tr;
                    break;
                case "fire":
                    model_data = (isTM) ? fire_tm : fire_tr;
                    break;
                case "water":
                    model_data = (isTM) ? water_tm : water_tr;
                    break;
                case "electric":
                    model_data = (isTM) ? electric_tm : electric_tr;
                    break;
                case "grass":
                    model_data = (isTM) ? grass_tm : grass_tr;
                    break;
                case "ice":
                    model_data = (isTM) ? ice_tm : ice_tr;
                    break;
                case "fighting":
                    model_data = (isTM) ? fighting_tm : fighting_tr;
                    break;
                case "poison":
                    model_data = (isTM) ? poison_tm : poison_tr;
                    break;
                case "ground":
                    model_data = (isTM) ? ground_tm : ground_tr;
                    break;
                case "flying":
                    model_data = (isTM) ? flying_tm : flying_tr;
                    break;
                case "psychic":
                    model_data = (isTM) ? psychic_tm : psychic_tr;
                    break;
                case "bug":
                    model_data = (isTM) ? bug_tm : bug_tr;
                    break;
                case "rock":
                    model_data = (isTM) ? rock_tm : rock_tr;
                    break;
                case "ghost":
                    model_data = (isTM) ? ghost_tm : ghost_tr;
                    break;
                case "dragon":
                    model_data = (isTM) ? dragon_tm : dragon_tr;
                    break;
                case "dark":
                    model_data = (isTM) ? dark_tm : dark_tr;
                    break;
                case "steel":
                    model_data = (isTM) ? steel_tm : steel_tr;
                    break;
                case "fairy":
                    model_data = (isTM) ? fairy_tm : fairy_tr;
                    break;
                default:
                    break;
            }
        }
        item.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(model_data)).add(DataComponentTypes.ITEM_NAME, (!move.equalsIgnoreCase("blank")) ?  ((isTM) ? Text.literal("TM: ") : Text.literal("TR: ")).append(Objects.requireNonNull(Moves.INSTANCE.getByName(move)).create().getDisplayName()) : Text.literal("Blank " + type.toUpperCase())).add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCompound)).build());
        return item;
    }
}

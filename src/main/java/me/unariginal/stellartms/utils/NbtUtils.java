package me.unariginal.stellartms.utils;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.component.DataComponentTypes.*;
import static net.minecraft.component.DataComponentTypes.CUSTOM_DATA;

public class NbtUtils {
    public static void setNbtString(@NotNull ItemStack itemStack, @NotNull String namespace, @NotNull String key, @NotNull String value) {
        if (key.isEmpty() || value.isEmpty()) {
            return;
        }

        itemStack.apply(CUSTOM_DATA, NbtComponent.DEFAULT, current -> {
            NbtCompound newNbt = current.copyNbt();

            if (namespace.isEmpty()) {
                // root namespace
                newNbt.putString(key, value);
                return NbtComponent.of(newNbt);
            }

            NbtCompound modNbt = newNbt.getCompound(namespace);
            modNbt.putString(key, value);
            newNbt.put(namespace, modNbt);
            return NbtComponent.of(newNbt);
        });
    }

    public static void setItemName(@NotNull ItemStack itemStack, @NotNull String value) {
        itemStack.applyComponentsFrom(ComponentMap.builder().add(ITEM_NAME, TextUtils.deserialize(value)).build());
    }

    public static void setItemLore(@NotNull ItemStack itemStack, @NotNull List<String> value) {
        List<Text> lore = new ArrayList<>();
        for (String line : value) {
            lore.add(TextUtils.deserialize(line));
        }
        itemStack.applyComponentsFrom(ComponentMap.builder().add(LORE, new LoreComponent(lore)).build());
    }

    @NotNull
    public static NbtCompound getNbt(@NotNull ItemStack itemStack, @NotNull String namespace) {
        NbtCompound nbt = itemStack.getOrDefault(CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (namespace.isEmpty()) {
            // root namespace
            return nbt;
        }

        return nbt.getCompound(namespace);
    }
}

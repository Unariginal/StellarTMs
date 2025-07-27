package me.unariginal.stellartms.utils;

import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.component.DataComponentTypes.*;

public class NbtUtils {
    public static void setItemName(@NotNull ItemStack itemStack, @NotNull String value) {
        itemStack.applyComponentsFrom(ComponentMap.builder().add(ITEM_NAME, TextUtils.deserialize(value)).build());
    }
}

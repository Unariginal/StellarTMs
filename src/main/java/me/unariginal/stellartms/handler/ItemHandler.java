package me.unariginal.stellartms.handler;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import me.unariginal.stellartms.StellarTMs;
import me.unariginal.stellartms.data.DataComponents;
import me.unariginal.stellartms.items.TM;
import me.unariginal.stellartms.items.TR;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemHandler {
    private static final Item.Settings settings = new Item.Settings().rarity(Rarity.UNCOMMON);
    public static TM StellarTM;
    public static TR StellarTR;

    public static void registerItems() {
        StellarTM = Registry.register(Registries.ITEM, Identifier.of(StellarTMs.MOD_ID, "tm"), new TM(settings.component(DataComponents.MOVE, "blank"), Items.BRICK));
        StellarTR = Registry.register(Registries.ITEM, Identifier.of(StellarTMs.MOD_ID, "tr"), new TR(settings.component(DataComponents.MOVE, "blank"), Items.NETHER_BRICK));
    }

    public static void registerItemGroup() {
        final ItemGroup StellarItems = FabricItemGroup.builder()
                .icon(StellarTM::getDefaultStack)
                .displayName(Text.literal("StellarTMs"))
                .entries((ctx, entries) -> {
                    entries.add(StellarTM);
                    entries.add(StellarTR);
                })
                .build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(StellarTMs.MOD_ID, "stellar_items"), StellarItems);
    }
}

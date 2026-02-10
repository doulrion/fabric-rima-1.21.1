package com.doulrion.rima.item;

import com.doulrion.rima.Rima;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class LockItems {

    public static final Item KEY_ITEM = registerItem("key", new Item(new Item.Settings()));
    public static final Item ADMIN_KEY_ITEM = registerItem("admin_key", new Item(new Item.Settings()));
    public static final Item LOCK_ITEM = registerItem("lock", new Item(new Item.Settings()));
    public static final ItemGroup LOCK_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Rima.MOD_ID, "lock_group"), FabricItemGroup.builder()
                    .icon(() -> new ItemStack(LOCK_ITEM))
                    .displayName(Text.translatable("itemGroup.rima.lock_group"))
                    .entries((context, entries) -> {
                        entries.add(KEY_ITEM);
                        entries.add(ADMIN_KEY_ITEM);
                        entries.add(LOCK_ITEM);
                    })
                    .build());

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Rima.MOD_ID, name), item);
    }

    public static void init() {
    }
}

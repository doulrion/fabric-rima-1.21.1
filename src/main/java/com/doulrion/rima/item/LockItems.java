package com.doulrion.rima.item;

import java.util.ArrayList;

import com.doulrion.rima.Rima;
import com.doulrion.rima.item.custom.KeyItem;
import com.doulrion.rima.item.custom.LockItem;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class LockItems {

    public static final Item KEY_ITEM = registerItem("key", new KeyItem(new Item.Settings()));
    public static final Item ADMIN_KEY_ITEM = registerItem("admin_key", new KeyItem(new Item.Settings()));
    public static final Item LOCKPICK_ITEM = registerItem("lockpick", new Item(new Item.Settings().maxDamage(100)));
    public static final Item LOCK_ITEM = registerItem("lock", new LockItem(new Item.Settings(),
                                            0.1F,
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE, GameMode.SPECTATOR},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR}
                                            ));
    public static final Item DIAMOND_LOCK_ITEM = registerItem("diamond_lock", new LockItem(new Item.Settings(),
                                            0.05F,
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE, GameMode.SPECTATOR},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR}
                                            ));
    public static final Item NETHERITE_LOCK_ITEM = registerItem("netherite_lock", new LockItem(new Item.Settings(),
                                            0.025F,
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE, GameMode.SPECTATOR},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR}
                                            ));
    public static final Item DUNGEON_LOCK_ITEM = registerItem("dungeon_lock", new LockItem(new Item.Settings(),
                                            0F,
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE, GameMode.SPECTATOR},
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR}
                                            ));
    public static final Item PICKABLE_DUNGEON_LOCK_ITEM = registerItem("pickable_dungeon_lock", new LockItem(new Item.Settings(),
                                            0.05F,
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE, GameMode.SPECTATOR},
                                            new GameMode[]{GameMode.SURVIVAL, GameMode.ADVENTURE, GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR}
                                            ));
    public static final Item ADMIN_LOCK_ITEM = registerItem("admin_lock", new LockItem(new Item.Settings(),
                                            0F,
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR},
                                            new GameMode[]{GameMode.CREATIVE},
                                            new GameMode[]{GameMode.CREATIVE, GameMode.SPECTATOR}
                                            ));
    public static final ItemGroup LOCK_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Rima.MOD_ID, "lock_group"), FabricItemGroup.builder()
                    .icon(() -> new ItemStack(LOCK_ITEM))
                    .displayName(Text.translatable("itemGroup.rima.lock_group"))
                    .entries((context, entries) -> {
                        entries.add(KEY_ITEM);
                        entries.add(ADMIN_KEY_ITEM);
                        entries.add(LOCKPICK_ITEM);
                        entries.add(LOCK_ITEM);
                        entries.add(DIAMOND_LOCK_ITEM);
                        entries.add(NETHERITE_LOCK_ITEM);
                        entries.add(DUNGEON_LOCK_ITEM);
                        entries.add(PICKABLE_DUNGEON_LOCK_ITEM);
                        entries.add(ADMIN_LOCK_ITEM);
                    })
                    .build());
    public static final ArrayList<Item> KeyItems = new ArrayList<Item>();
    public static final ArrayList<Item> LockItems = new ArrayList<Item>();

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Rima.MOD_ID, name), item);
    }

    public static void init() {
      // add keys to list
      KeyItems.add(ADMIN_KEY_ITEM);
      KeyItems.add(KEY_ITEM);
      // add locks to list
      LockItems.add(ADMIN_LOCK_ITEM);
      LockItems.add(LOCK_ITEM);
      LockItems.add(DIAMOND_LOCK_ITEM);
      LockItems.add(NETHERITE_LOCK_ITEM);
      LockItems.add(DUNGEON_LOCK_ITEM);
      LockItems.add(PICKABLE_DUNGEON_LOCK_ITEM);
    }

}

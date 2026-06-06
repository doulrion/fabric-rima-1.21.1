// com/doulrion/rima/blockentity/RimaBlockEntityTypes.java
package com.doulrion.rima.blockentity;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RimaBlockEntityTypes {

    public static BlockEntityType<LockedDoorBlockEntity> LOCKED_DOOR;

    public static void register() {
        LOCKED_DOOR = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("rima", "locked_door"),
            BlockEntityType.Builder.create(
                (pos, state) -> new LockedDoorBlockEntity(LOCKED_DOOR, pos, state),
                Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR,
                Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR,
                Blocks.MANGROVE_DOOR, Blocks.CHERRY_DOOR, Blocks.BAMBOO_DOOR,
                Blocks.CRIMSON_DOOR, Blocks.WARPED_DOOR, Blocks.IRON_DOOR
            ).build()
        );
    }
}
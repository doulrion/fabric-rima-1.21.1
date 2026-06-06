// com/doulrion/rima/mixin/DoorBlockEntityMixin.java
package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.LockedDoorBlockEntity;
import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;

@Mixin(DoorBlock.class)
@Implements(@Interface(iface = BlockEntityProvider.class, prefix = "rima$"))
public abstract class DoorBlockEntityMixin {

    // Only attach a block entity to the lower half to avoid doubling
    public BlockEntity rima$createBlockEntity(BlockPos pos, BlockState state) {
        if (state.contains(Properties.DOUBLE_BLOCK_HALF) &&
            state.get(Properties.DOUBLE_BLOCK_HALF) == net.minecraft.block.enums.DoubleBlockHalf.LOWER) {
            return new LockedDoorBlockEntity(RimaBlockEntityTypes.LOCKED_DOOR, pos, state);
        }
        return null;
    }
}
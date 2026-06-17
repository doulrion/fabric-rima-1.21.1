// com/doulrion/rima/mixin/DoorBlockEntityMixin.java
package com.doulrion.rima.mixin;

import com.doulrion.rima.Rima;
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

    private static final String[] blacklist = new String[] {
        // "yuushya"
    };
    // Only attach a block entity to the lower half to avoid doubling
    public BlockEntity rima$createBlockEntity(BlockPos pos, BlockState state) {
        if ( ! isOnBlacklist( state.getBlock().getName().toString() )
            && state.contains(Properties.DOUBLE_BLOCK_HALF) 
            && state.get(Properties.DOUBLE_BLOCK_HALF) == net.minecraft.block.enums.DoubleBlockHalf.LOWER) {
            if (!RimaBlockEntityTypes.LOCKED_DOOR.supports(state)){
              RimaBlockEntityTypes.LOCKED_DOOR.addSupportedBlock(state.getBlock());     // dynamically add support for blocks
              Rima.LOGGER.info("added DoorBlockEntity support for block: " + state.getBlock().getName().toString());
            }
            return new LockedDoorBlockEntity(RimaBlockEntityTypes.LOCKED_DOOR, pos, state);
        }
        return null;
    }

    private boolean isOnBlacklist(String BlockName) {
        for (String blacklistedBlock : blacklist) {
            if (BlockName.contains(blacklistedBlock)) {
                return true;
            }
        }
        return false;
    }
}
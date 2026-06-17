// com/doulrion/rima/mixin/DoorBlockEntityMixin.java
package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;

@Mixin(LeverBlock.class)
@Implements(@Interface(iface = BlockEntityProvider.class, prefix = "rima$"))
public abstract class LeverBlockEntityMixin {

    // private static final String[] blacklist = new String[] {
    //     // "yuushya"
    // };
    // Only attach a block entity to the lower half to avoid doubling

    public BlockEntity rima$createBlockEntity(BlockPos pos, BlockState state) {
      return RimaBlockEntityTypes.registerAndCreateLockedRimaEntity(pos, state);
    }
}
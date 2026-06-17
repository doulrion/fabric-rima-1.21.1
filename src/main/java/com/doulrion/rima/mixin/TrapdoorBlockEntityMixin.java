package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;

@Mixin(TrapdoorBlock.class)
@Implements(@Interface(iface = BlockEntityProvider.class, prefix = "rima$"))
public abstract class TrapdoorBlockEntityMixin {

    private static final String[] blacklist = new String[] {
        // "yuushya"
    };
    // Only attach a block entity to the lower half to avoid doubling
    public BlockEntity rima$createBlockEntity(BlockPos pos, BlockState state) {
        if ( ! isOnBlacklist( state.getBlock().getName().toString())){
            return RimaBlockEntityTypes.registerAndCreateLockedRimaEntity(pos, state);
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
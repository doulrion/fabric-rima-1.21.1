package com.doulrion.rima.mixin;

import com.doulrion.rima.component.RimaLockState;
import com.doulrion.rima.component.RimaHelper;
import com.doulrion.rima.interfaces.ILockableRimaEntity;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

  @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
  private void getHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir){
    BlockEntity be = world.getBlockEntity(RimaHelper.normalizeBlockPos(world.getBlockState(pos), pos));
    if (!(be instanceof ILockableRimaEntity le)){
      return;
    }
    RimaLockState state = le.getLockState();  
    if (!state.isLocked()){
      return;
    }
    cir.setReturnValue(Blocks.BEDROCK.getHardness());

    // if (!state.isPlayerRemovable(null)){       // might be interesting later on
    //   cir.setReturnValue(Blocks.BEDROCK.getHardness());
    //   return;
    // }
    // float f = cir.getReturnValueF();
    // // if ()
    // //    * 200;
    //   // Rima.LOGGER.info("changed hardness: " + Float.toString(f));
    // cir.setReturnValue(f * 200);
    
  }

}
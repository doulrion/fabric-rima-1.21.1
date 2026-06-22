package com.doulrion.rima.mixin;

import com.doulrion.rima.component.RimaLockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public class DoorBlockMixin {

  @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
  private void rima$onUse(BlockState state, World world, BlockPos pos,
                           PlayerEntity player, BlockHitResult hit,
                           CallbackInfoReturnable<ActionResult> cir) {

      RimaLockState.onUseGenericBlock(state, world, pos, player, hit, cir);

  }

  @Inject(method = "neighborUpdate", at = @At("HEAD"), cancellable = true)
  private void rima$neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify,
                            CallbackInfo cir) {
    RimaLockState.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify, cir);
    
  }
}
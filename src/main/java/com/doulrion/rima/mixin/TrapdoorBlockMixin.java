package com.doulrion.rima.mixin;

import com.doulrion.rima.component.RimaGenericBlockLockHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
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

@Mixin(TrapdoorBlock.class)
public class TrapdoorBlockMixin {

  @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
  private void rima$onUse(BlockState state, World world, BlockPos pos,
                           PlayerEntity player, BlockHitResult hit,
                           CallbackInfoReturnable<ActionResult> cir) {
    RimaGenericBlockLockHelper.onUseGenericBlock(state, world, pos, player, hit, cir);
  }

  @Inject(method = "neighborUpdate", at = @At("HEAD"), cancellable = true)
  private void rima$neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify,
                            CallbackInfo cir) {
    if (RimaGenericBlockLockHelper.neighborUpdate(state, world, pos)){
      cir.cancel();
    }
  }

}
package com.doulrion.rima.mixin;

import com.doulrion.rima.component.RimaLockState;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeverBlock.class)
public class LeverBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void rima$onUse(BlockState state, World world, BlockPos pos,
                             PlayerEntity player, BlockHitResult hit,
                             CallbackInfoReturnable<ActionResult> cir) {
      RimaLockState.onUseGenericBlock(state, world, pos, player, hit, cir);
    }

}
package com.doulrion.rima.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.ActionResult;


public interface ILockableRimaEntity {
    void setKey(String key);
    String getKey();
    public void setAdminLocked(boolean adminLocked);
    public boolean isAdminLocked();
    boolean isLocked();
    boolean doesUnlock(String key);
    public void HandleOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir);
    public boolean canLockpick(PlayerEntity player, net.minecraft.item.ItemStack heldStack);
}
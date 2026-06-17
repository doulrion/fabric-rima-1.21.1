package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.LockedDoorBlockEntity;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.item.LockItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        if (world.isClient) return;

        BlockEntity be = getLowerBlockEntity(world, pos, state);

        if (!IsLockedLockedDoorEntity(be)) {
            return; // unlocked — let vanilla handle it
        }

        ItemStack held = player.getMainHandStack();

        // Admin key bypasses everything
        if (held.isOf(LockItems.ADMIN_KEY_ITEM) || player.isCreative()) {
            return;
        }

        // Normal key check
        if (held.isOf(LockItems.KEY_ITEM)) {
            String heldId = held.get(RimaDataComponentTypes.RIMA_LOCK);
            if (heldId != null && ((LockedDoorBlockEntity) be).doesUnlock(heldId)) {
                return;
            }
        }

        if (canLockpick(player, held)) {
            return;
        }

        player.sendMessage(Text.translatable("message.rima.door_is_locked"), true);
        cir.setReturnValue(ActionResult.FAIL);
    }

    @Inject(method = "neighborUpdate", at = @At("HEAD"), cancellable = true)
    private void rima$neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify,
                              CallbackInfo cir) {
      if (IsLockedLockedDoorEntity(getLowerBlockEntity(world, pos, state))) {
        // Prevent BlockUpdate from opening the door if it's locked. Only Player can Unlock
        cir.cancel();
        return;
      }
      
    }

    private BlockEntity getLowerBlockEntity(World world, BlockPos pos, BlockState state) {
        if (state.contains(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF)){
          if (state.get(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER) {
            return world.getBlockEntity(pos.down());
          } else {
            return world.getBlockEntity(pos);
          }
        } else {
            return null;
        }
    }

    private boolean IsLockedLockedDoorEntity (BlockEntity be) {
        if (be instanceof LockedDoorBlockEntity door) {
            return door.isLocked();
        }
        return false;
    }

    @Unique
    private boolean canLockpick(PlayerEntity player, ItemStack heldStack) {
        if (!heldStack.isOf(LockItems.LOCKPICK_ITEM)) {
            return false;
        }

        if (player.getRandom().nextFloat() < 0.05F) {
            return true;
        }

        heldStack.damage(Math.max(1, heldStack.getMaxDamage() / 2), player, EquipmentSlot.MAINHAND);
        return false;
    }
}
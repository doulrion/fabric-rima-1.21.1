package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.LockedDoorBlockEntity;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.item.LockItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        if (world.isClient) return;

        // Doors are two blocks tall; normalise to the lower half
        BlockPos lowerPos = isDoorUpper(state) ? pos.down() : pos;
        BlockEntity be = world.getBlockEntity(lowerPos);

        if (!(be instanceof LockedDoorBlockEntity door)) return;
        if (!door.isLocked()) return; // unlocked — let vanilla handle it

        ItemStack held = player.getMainHandStack();

        // Admin key bypasses everything
        if (held.isOf(LockItems.ADMIN_KEY_ITEM)) {
            return;
        }

        // Normal key check
        if (held.isOf(LockItems.KEY_ITEM)) {
            String heldId = held.get(RimaDataComponentTypes.RIMA_LOCK);
            if (heldId != null && door.doesUnlock(heldId)) {
                return;
            }
        }

        player.sendMessage(Text.literal("This door is locked."), true);
        cir.setReturnValue(ActionResult.FAIL);
    }

    private boolean isDoorUpper(BlockState state) {
    return state.contains(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF)
        && state.get(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF)
           == net.minecraft.block.enums.DoubleBlockHalf.UPPER;
    }

    @Inject(method = "onBreak", at = @At("HEAD"), cancellable = true)
    private void rima$onBreak(World world, BlockPos pos, BlockState state,
                           PlayerEntity player,
                           CallbackInfoReturnable<BlockState> cir) {
    if (world.isClient) return;

    BlockPos lowerPos = isDoorUpper(state) ? pos.down() : pos;
    BlockEntity be = world.getBlockEntity(lowerPos);

    if (!(be instanceof LockedDoorBlockEntity door)) return;
    if (!door.isLocked()) return;

    ItemStack held = player.getMainHandStack();
    if (held.isOf(LockItems.ADMIN_KEY_ITEM)) return;

    player.sendMessage(Text.literal("You cannot break a locked door."), true);
    cir.cancel();  // still works the same way
    }
}
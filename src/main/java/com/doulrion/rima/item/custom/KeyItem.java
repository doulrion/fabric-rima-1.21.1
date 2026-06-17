package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.blockentity.LockedRimaBlockEntity;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.item.LockItems;

import java.util.List;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }

    public String getLockKey(ItemStack stack) {
      return isAdmin(stack) ? LockedRimaBlockEntity.adminUUID : stack.get(RimaDataComponentTypes.RIMA_LOCK);
    }

    public boolean isAdmin(ItemStack stack) {
        return stack.isOf(LockItems.ADMIN_KEY_ITEM);
    }

    private boolean canUnlock(ILockableRimaEntity lockableEntity, ItemStack stack) {
        return isAdmin(stack) || (getLockKey(stack).equals(lockableEntity.getKey()) && !lockableEntity.isAdminLocked());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        String lockKey = getLockKey(stack);
        if (isAdmin(stack) || getLockKey(stack) == null) {
            return;
        }

        tooltip.add(Text.translatable("tooltip.rima.lock_uuid", lockKey).formatted(Formatting.GRAY));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        // handle unlocking & action passing for locked blocks

        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (player == null
                || pos == null) {
            return ActionResult.PASS;
        }
        
        var blockState = context.getWorld().getBlockState(pos); 
        pos = blockState.contains(Properties.DOUBLE_BLOCK_HALF) &&
                    blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos; // get lower block for double blocks such as doors

        BlockEntity blockEntity = context.getWorld().getBlockEntity(pos); // get block entity at position

        if (!(blockEntity instanceof ILockableRimaEntity lockableEntity) || !lockableEntity.isLocked()){  // block isnt lockable or is not locked
            return ActionResult.PASS;
        }

        if (!canUnlock(lockableEntity, stack)) {   // cant unlock. return
            player.sendMessage(Text.translatable("message.rima.wrong_key"), true);
            return player.isSneaking() ? ActionResult.PASS : ActionResult.FAIL;   // if not sneaking prevent accidental block placement
        }

        if (player.isSneaking()){   // player is sneaking, unlock.
          if (!context.getWorld().isClient()) {
            ItemStack droppedLock = new ItemStack(lockableEntity.isAdminLocked() ? LockItems.ADMIN_LOCK_ITEM : LockItems.LOCK_ITEM);
            if (!lockableEntity.isAdminLocked() && lockableEntity.getKey() != null) {   // only assign id if needed.
                droppedLock.set(RimaDataComponentTypes.RIMA_LOCK, lockableEntity.getKey());
            }
            ItemScatterer.spawn(context.getWorld(), pos.getX(), pos.getY(), pos.getZ(), droppedLock);
            lockableEntity.setKey(null);
            player.sendMessage(Text.translatable("message.rima.unlocked"), true);
            return ActionResult.SUCCESS;
          }
        } 
        return ActionResult.PASS; // allow default interaction        
    }
}

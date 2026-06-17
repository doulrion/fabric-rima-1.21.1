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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LockItem extends Item {

    public LockItem(Settings settings) {
        super(settings);
    }

    public String getLockKey(ItemStack stack) {
      return isAdmin(stack) ? LockedRimaBlockEntity.adminUUID : stack.get(RimaDataComponentTypes.RIMA_LOCK);
    }

    public boolean isAdmin(ItemStack stack) {
        return stack.isOf(LockItems.ADMIN_LOCK_ITEM);
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (isAdmin(stack)) {
            super.onCraft(stack, world);
            return;
        }

        if (stack.get(RimaDataComponentTypes.RIMA_LOCK) != null) {
            return;
        }
        String uniqueID = java.util.UUID.randomUUID().toString();
        stack.set(RimaDataComponentTypes.RIMA_LOCK, uniqueID);
        super.onCraft(stack, world);
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
        ItemStack stack = context.getStack();
        String lockKey = getLockKey(stack);
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (player == null
                || pos == null) {
            return ActionResult.PASS;
        }

        var blockState = context.getWorld().getBlockState(pos);

        pos = ( blockState.contains(Properties.DOUBLE_BLOCK_HALF)   // compensate for double blocks (doors) 
                && blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) ? 
            pos.down() : pos;
        
        BlockEntity blockEntity = context.getWorld().getBlockEntity(pos);

        if (!(player.isSneaking() && blockEntity instanceof ILockableRimaEntity lockableEntity)) {   // PASS if not lockable
            return ActionResult.PASS; 
        }

        if (!isAdmin(stack) && lockKey == null) { // fail on non admin interaction
            return ActionResult.FAIL;
        }

        if (lockableEntity.isLocked()) {    // fail if already locked
            player.sendMessage(Text.translatable("message.rima.already_locked"), true);
            return ActionResult.FAIL;
        }

        lockableEntity.setKey(lockKey);   // lock block (admin UUID already set)
        player.sendMessage(Text.translatable(isAdmin(stack) ? "message.rima.admin_locked" : "message.rima.locked"), true);
        stack.decrement(1);
        return ActionResult.SUCCESS;
    }

}

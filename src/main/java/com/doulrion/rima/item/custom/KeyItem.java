package com.doulrion.rima.item.custom;

import com.doulrion.rima.blockentity.LockedDoorBlockEntity;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableContainerBlockEntity;
import com.doulrion.rima.item.LockItems;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }

    private boolean canUnlock(ILockableContainerBlockEntity lockableContainer, ItemStack stack) {
        if (stack.isOf(LockItems.ADMIN_KEY_ITEM)) {
            return true;
        }

        return !lockableContainer.isAdminLocked()
                && Objects.equals(stack.get(RimaDataComponentTypes.RIMA_LOCK), lockableContainer.getKey());
    }

    private boolean canUnlock(LockedDoorBlockEntity door, ItemStack stack) {
        if (stack.isOf(LockItems.ADMIN_KEY_ITEM)) {
            return true;
        }

        return !door.isAdminLocked()
                && Objects.equals(stack.get(RimaDataComponentTypes.RIMA_LOCK), door.getKey());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        String lockKey = stack.get(RimaDataComponentTypes.RIMA_LOCK);
        if (lockKey == null) {
            return;
        }

        MutableText tooltipText = Text.translatable("tooltip.rima.lock_uuid", lockKey).formatted(Formatting.GRAY);
        tooltip.add(tooltipText);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (player == null
                || pos == null) {
            return ActionResult.PASS;
        }

        BlockEntity blockEntity = context.getWorld().getBlockEntity(pos);
        var blockState = context.getWorld().getBlockState(pos);
        if (blockState.getBlock() instanceof DoorBlock) {
            var targetPos = blockState.contains(Properties.DOUBLE_BLOCK_HALF) &&
                    blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER
                    ? pos.down() : pos;
            blockEntity = context.getWorld().getBlockEntity(targetPos);
        }

        if (!player.isSneaking()) {
            return ActionResult.PASS;
        }

        if (blockEntity instanceof ILockableContainerBlockEntity container 
            && container.isLocked() 
            && canUnlock(container, stack)) {
                boolean adminLocked = container.isAdminLocked();
                String lockKey = container.getKey();
                container.setAdminLocked(false);
                container.setKey(null); // unlock chest
                if (!context.getWorld().isClient()) {
                    ItemStack droppedLock = new ItemStack(adminLocked ? LockItems.ADMIN_LOCK_ITEM : LockItems.LOCK_ITEM);
                    if (!adminLocked && lockKey != null) {
                        droppedLock.set(RimaDataComponentTypes.RIMA_LOCK, lockKey);
                    }
                    ItemScatterer.spawn(context.getWorld(), pos.getX(), pos.getY(), pos.getZ(), droppedLock);
                    player.sendMessage(Text.translatable("message.rima.chest_unlocked"), false);
                }
                return ActionResult.SUCCESS;
        }

        if (blockEntity instanceof LockedDoorBlockEntity door 
            && door.isLocked() 
            && canUnlock(door, stack)) {
                boolean adminLocked = door.isAdminLocked();
                String lockKey = door.getKey();
                door.setAdminLocked(false);
                door.setKey(null); // unlock door
                if (!context.getWorld().isClient()) {
                    ItemStack droppedLock = new ItemStack(adminLocked ? LockItems.ADMIN_LOCK_ITEM : LockItems.LOCK_ITEM);
                    if (!adminLocked && lockKey != null) {
                        droppedLock.set(RimaDataComponentTypes.RIMA_LOCK, lockKey);
                    }
                    ItemScatterer.spawn(context.getWorld(), pos.getX(), pos.getY(), pos.getZ(), droppedLock);
                    player.sendMessage(Text.translatable("message.rima.door_unlocked"), false);
                }
                return ActionResult.SUCCESS;
        }
        

        return ActionResult.PASS;
    }
}

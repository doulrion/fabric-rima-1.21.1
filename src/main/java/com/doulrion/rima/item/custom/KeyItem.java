package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableContainerBlockEntity;
import com.doulrion.rima.item.LockItems;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
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
import net.minecraft.world.World;

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
        if (!player.isSneaking()
                || !(blockEntity instanceof LockableContainerBlockEntity)) {
            return ActionResult.PASS;
        }
        ILockableContainerBlockEntity lCon = (ILockableContainerBlockEntity) (Object) blockEntity;
        if (!lCon.isLocked()) { // dont do anything. chest isnt even locked
            return ActionResult.PASS;
        }
        if (!canUnlock(lCon, stack)) {
            player.sendMessage(Text.literal("Can not Unlock. Mismatched Keys!"), false);
            return ActionResult.PASS;
        }

        boolean adminLocked = lCon.isAdminLocked();
        String lockKey = lCon.getKey();

        lCon.setAdminLocked(false);
        lCon.setKey(null); // unlock chest
        if (!context.getWorld().isClient()) {
            ItemStack droppedLock = new ItemStack(adminLocked ? LockItems.ADMIN_LOCK_ITEM : LockItems.LOCK_ITEM);
            if (!adminLocked && lockKey != null) {
                droppedLock.set(RimaDataComponentTypes.RIMA_LOCK, lockKey);
            }
            ItemScatterer.spawn(context.getWorld(), pos.getX(), pos.getY(), pos.getZ(), droppedLock);
        }
        player.sendMessage(Text.literal("Debug: Chest has been Unlocked!"), false);

        return ActionResult.SUCCESS;
    }
}

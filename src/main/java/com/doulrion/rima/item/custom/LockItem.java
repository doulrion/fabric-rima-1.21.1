package com.doulrion.rima.item.custom;

import com.doulrion.rima.blockentity.LockedDoorBlockEntity;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableContainerBlockEntity;

import java.util.List;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LockItem extends Item {
    private final boolean adminOnly;

    public LockItem(Settings settings) {
        this(settings, false);
    }

    public LockItem(Settings settings, boolean adminOnly) {
        super(settings);
        this.adminOnly = adminOnly;
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (adminOnly) {
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

        if (adminOnly) {
            return;
        }

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
        String lockKey = stack.get(RimaDataComponentTypes.RIMA_LOCK);
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

        if (!player.isSneaking()
                || (!adminOnly && lockKey == null)) {
            return ActionResult.PASS;
        }

        if (blockEntity instanceof LockedDoorBlockEntity door && !door.isLocked()) {
          String lockId = stack.get(RimaDataComponentTypes.RIMA_LOCK);
          if (lockId != null) {
            door.setAdminLocked(adminOnly);
            door.setKey(adminOnly ? null : lockKey);
            stack.decrement(1);
            player.sendMessage(Text.translatable(adminOnly ? "message.rima.door_admin_locked" : "message.rima.door_locked"), true);
            return ActionResult.SUCCESS;
          }
        }

        if (blockEntity instanceof ILockableContainerBlockEntity container && !container.isLocked()) {
          String lockId = stack.get(RimaDataComponentTypes.RIMA_LOCK);
          if (lockId != null) {
            container.setAdminLocked(adminOnly);
            container.setKey(adminOnly ? null : lockKey);
            stack.decrement(1);
            player.sendMessage(Text.translatable(adminOnly ? "message.rima.chest_admin_locked" : "message.rima.chest_locked"), true);
            return ActionResult.SUCCESS;
          }
        }

        if (blockEntity instanceof LockedDoorBlockEntity door && door.isLocked()) {
            player.sendMessage(Text.translatable("message.rima.door_already_locked"), false);
            return ActionResult.PASS;
        }

        if (blockEntity instanceof ILockableContainerBlockEntity container && container.isLocked()) {
            player.sendMessage(Text.translatable("message.rima.chest_already_locked"), false);
            return ActionResult.PASS;
        }

        return ActionResult.SUCCESS;
    }

}

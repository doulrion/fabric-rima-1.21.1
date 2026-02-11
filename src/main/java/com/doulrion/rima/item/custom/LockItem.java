package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.IntfLockableContainerBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LockItem extends Item {
    public LockItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (stack.get(RimaDataComponentTypes.RIMA_LOCK) != null) {
            return;
        }
        String uniqueID = java.util.UUID.randomUUID().toString();
        stack.set(RimaDataComponentTypes.RIMA_LOCK, uniqueID);
        super.onCraft(stack, world);
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
                || !(blockEntity instanceof LockableContainerBlockEntity)
                || stack.get(RimaDataComponentTypes.RIMA_LOCK) == null) {
            return ActionResult.PASS;
        }
        IntfLockableContainerBlockEntity lCon = (IntfLockableContainerBlockEntity) (Object) blockEntity;
        if (lCon.isLocked()) { // do not lock if already locked
            player.sendMessage(Text.literal("Can not lock. Chest is already locked!"), false);
            return ActionResult.PASS;
        }
        lCon.setKey(stack.get(RimaDataComponentTypes.RIMA_LOCK)); // sets key
        stack.decrement(1);

        player.sendMessage(Text.literal("Chest has been locked"), false);

        return ActionResult.SUCCESS;
    }

}

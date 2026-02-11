package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.IntfLockableContainerBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.Text;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        super.onCraft(stack, world);
    }

    private boolean canUnlock(String lockKey, ItemStack stack) {
        if (stack.getItem().toString().equals("rima:admin_key")) {
            return true;
        } else {
            String keyKey = stack.get(RimaDataComponentTypes.RIMA_LOCK);
            return (keyKey != null)
                    && (keyKey == lockKey);
        }
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
        IntfLockableContainerBlockEntity lCon = (IntfLockableContainerBlockEntity) (Object) blockEntity;
        if (!lCon.isLocked()) { // dont do anything. chest isnt even locked
            return ActionResult.PASS;
        }
        if (!canUnlock(lCon.getKey(), stack)) {
            player.sendMessage(Text.literal("Can not Unlock. Missmatched Keys!"), false);
            return ActionResult.PASS;
        }

        lCon.setKey(null); // unlock chest
        player.sendMessage(Text.literal("Debug: Chest has been Unlocked!"), false);
        // TODO: Drop Lock Item

        return ActionResult.SUCCESS;
    }
}

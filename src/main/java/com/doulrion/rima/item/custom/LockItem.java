package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;

import java.lang.reflect.Method;
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
        if(stack.get(RimaDataComponentTypes.RIMA_LOCK) != null) {
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

        if(player == null 
            || pos == null) {
            return ActionResult.PASS;
        }

        BlockEntity blockEntity = context.getWorld().getBlockEntity(pos);
        if(!player.isSneaking() 
            || !(blockEntity instanceof ChestBlockEntity) 
            || stack.get(RimaDataComponentTypes.RIMA_LOCK) == null) {
            return ActionResult.PASS;
        }

        // TODO: Implement locking logic here.

        return ActionResult.SUCCESS;
    }

}

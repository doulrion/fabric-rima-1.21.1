package com.doulrion.rima.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void onCraft(ItemStack stack, World world) {
        super.onCraft(stack, world);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }
}

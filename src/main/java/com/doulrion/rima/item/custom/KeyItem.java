package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.component.RimaLockState;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

public class KeyItem extends Item {
    public KeyItem(Settings settings) {
        super(settings);
    }

    public String getLockKey(ItemStack stack) {
      return stack.contains(RimaDataComponentTypes.RIMA_LOCK) ? stack.get(RimaDataComponentTypes.RIMA_LOCK) : null;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        String lockKey = getLockKey(stack);
        if (lockKey != null) {
          tooltip.add(Text.translatable("tooltip.rima.lock_uuid", lockKey).formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
      return RimaLockState.useOnBlockKey(context);
    }
}

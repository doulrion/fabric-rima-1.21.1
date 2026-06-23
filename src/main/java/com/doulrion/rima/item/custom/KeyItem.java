package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.component.RimaLockState;
import com.doulrion.rima.component.RimaHelper;
import com.doulrion.rima.interfaces.ILockableRimaEntity;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.GameMode;

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
    PlayerEntity player = context.getPlayer();
    if (!player.isSneaking()){
      return ActionResult.PASS;
    }
    ItemStack stack = context.getStack();
    GameMode gameMode = RimaHelper.getPlayerGamemode(player);
    World world = context.getWorld();
    BlockPos pos = RimaHelper.normalizeBlockPos(context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());

    if (!(world.getBlockEntity(pos) instanceof ILockableRimaEntity le)){
      return ActionResult.PASS;
    }
    
    RimaLockState lockState = le.getLockState();  // not locked.
    if (lockState == null || !lockState.isLocked()){
      return ActionResult.PASS;
    }

    if (RimaHelper.isKeyItem(stack)){
      return lockState.doRemoveLock(le, world, pos, player, gameMode, stack);
    }
    return ActionResult.SUCCESS_NO_ITEM_USED;
  }
}

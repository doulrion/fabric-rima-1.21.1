package com.doulrion.rima.item.custom;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.blockentity.LockedRimaBlockEntity;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.component.RimaLockState;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity.Action;
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
import net.minecraft.world.World;
import net.minecraft.world.GameMode;

public class LockItem extends Item {
    public final float defaultPickRate;
    public final GameMode[] defaultGameModeAdd;
    public final GameMode[] defaultGameModeRemove;
    public final GameMode[] defaultGameModeUse;
    public final GameMode[] defaultGameModePick;
    public final GameMode[] defaultGameModeBypassUse;

    public LockItem(Settings settings, float pickRate, GameMode[] Add, GameMode[] Remove, GameMode[] Use, GameMode[] Pick, GameMode[] BypassUse) {
        super(settings);
        defaultPickRate = pickRate;
        defaultGameModeAdd = Add;
        defaultGameModeRemove = Remove;
        defaultGameModeUse = Use;
        defaultGameModePick = Pick;
        defaultGameModeBypassUse = BypassUse;
    }

    public String getLockKey(ItemStack stack) {
      return stack.contains(RimaDataComponentTypes.RIMA_LOCK) ? stack.get(RimaDataComponentTypes.RIMA_LOCK) : null;
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
        String uniqueID = UUID.randomUUID().toString();
        stack.set(RimaDataComponentTypes.RIMA_LOCK, uniqueID);
        super.onCraft(stack, world);
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
      return RimaLockState.useOnBlockLock(context);
    }

}

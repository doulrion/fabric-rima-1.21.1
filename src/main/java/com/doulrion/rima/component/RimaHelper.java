package com.doulrion.rima.component;

import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.text.Text;

import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.Rima;

public class RimaHelper {
  public class Messages{
    // creative messages
    public static void messageBypassed(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.bypassed"), true);}

    // pick messages
    public static void messagePickSuccess(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.pick_success"), true);}
    public static void messagePickFailed(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.pick_failed"), true);}
    public static void messagePickNotAllowed(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.pick_not_allowed"), true);}
    
    // use Messages
    public static void messageUseSuccess(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.use_success"), true);}
    public static void messageLockedNoKey(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.locked_no_key"), true);}
    public static void messageWrongKey(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.wrong_key"), true);}
    public static void messageUseNotAllowed(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.use_not_allowed"), true);}

    // lock messages
    public static void messageLockInvalid(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.lock_invalid"), true);}
    public static void messageLockAdded(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.lock_added"), true);}
    public static void messageLockAddNotAllowed(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.add_not_allowed"), true);}
    public static void messageLockRemoved(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.lock_removed"), true);}
    public static void messageLockRemoveNotAllowed(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.remove_not_allowed"), true);}
    public static void messageNotBreakable(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.not_breakable"), true);}
    public static void messageAlreadyLocked(PlayerEntity player){player.sendMessage(Text.translatable("message.rima.already_locked"), true);}
  }

  public static UUID getKeyFromStack(ItemStack Stack){
    String itemKey = Stack.get(RimaDataComponentTypes.RIMA_LOCK);
    if (itemKey == null){
      return null;
    }
    try {
      return UUID.fromString(itemKey);
    } catch (Exception e) {
      Rima.LOGGER.error("Could not parse invalid lock item UUID: " + itemKey, e);
      return null;
    }
  }

  public static GameMode getPlayerGamemode(PlayerEntity player){
    if (player.isCreative()){
      return GameMode.CREATIVE;
    } else if (player.isSpectator()) {
      return GameMode.SPECTATOR;
    } else if (player.canModifyBlocks()){
      return GameMode.SURVIVAL;
    } else {
      return GameMode.ADVENTURE;
    }
  }

  public static boolean isKeyItem(ItemStack stack){
    return stack.isOf(LockItems.KEY_ITEM) || stack.isOf(LockItems.ADMIN_KEY_ITEM);
  }  
  
  public static boolean isLockItem(ItemStack stack){
    return LockItems.LockItems.contains(stack.getItem());
  }

  public static boolean isPickItem(ItemStack stack){
    return stack.isOf(LockItems.LOCKPICK_ITEM);
  }

  public static BlockPos normalizeBlockPos(BlockState state, BlockPos pos){
    if (state.contains(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF) && 
      state.get(net.minecraft.state.property.Properties.DOUBLE_BLOCK_HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER) {
        return pos.down();
    }
    return pos;
  }
}

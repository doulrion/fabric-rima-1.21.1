package com.doulrion.rima.component;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.interfaces.ILockableRimaEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.GameMode;
import net.minecraft.text.Text;

public class RimaGenericBlockLockHelper extends Object{


  // onUseGenericBlock for Generic block with LockableEntity
  public static void onUseGenericBlock(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
    // if (world.isClient) return;   // do not handle on client.

    pos = RimaHelper.normalizeBlockPos(state, pos);
      
    BlockEntity be = world.getBlockEntity(pos);

    if (!(be instanceof ILockableRimaEntity le)) return; // ignore non lockable entity

    RimaLockState lockstate = le.getLockState();
    
    ItemStack held = player.getMainHandStack();

    GameMode gameMode = RimaHelper.getPlayerGamemode(player); 

    if (!lockstate.isLocked()) return; // not locked. let vanilla handle it

    if (player.isSneaking()){   // try unlock
      if (held.isEmpty() && gameMode == GameMode.CREATIVE){
        player.sendMessage(Text.of(lockstate.debugString()), false);
        cir.setReturnValue(ActionResult.SUCCESS);
        return;
      } else {
        cir.setReturnValue(ActionResult.FAIL); // allow placing of blocks
        return;  
      }
    } else {
      if (lockstate.isGameModeBypassUse(gameMode)){  // bypass using 
        RimaHelper.Messages.messageBypassed(player);
        return;
      } else if (RimaHelper.isKeyItem(held)){
        if(lockstate.doOpenLock(player, gameMode, held)){
          cir.setReturnValue(ActionResult.SUCCESS);
        };
      } else if (RimaHelper.isPickItem(held)){
        if(lockstate.doPickLock(player, gameMode, held)){
          cir.setReturnValue(ActionResult.SUCCESS);
        }
      } else {
        RimaHelper.Messages.messageLockedNoKey(player);
        cir.setReturnValue(ActionResult.CONSUME);
      }
    }    
  } 

  // neighborUpdate for GenericBlock
  public static boolean neighborUpdate(BlockState state, World world, BlockPos pos){
    // if (world.isClient) return false;   // do not handle on client.
    if (!(world.getBlockEntity(RimaHelper.normalizeBlockPos(state, pos)) instanceof ILockableRimaEntity doorEntity)){
      return false;
    }
    return (doorEntity.getLockState().isLocked());
  }

}
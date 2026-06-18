package com.doulrion.rima.mixin;

import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.state.property.Properties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.Rima;
import com.doulrion.rima.blockentity.LockedRimaBlockEntity;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.item.LockItems;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements ILockableRimaEntity {

    @Unique
    private String lockKey = null;

    @Unique
    public void setKey(String key, boolean skipDblChestCheck) {
      if (skipDblChestCheck){
        lockKey = key;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
      } else {
        ((LockableContainerBlockEntityMixin) (Object) getLockedEntity()).setKey(key, true);   // does this actually work????
      }
    }

    @Unique
    public void setKey(String key) {
      setKey(key, false);
    }

    @Unique
    public String getKey(boolean skipDblChestCheck){
      if (skipDblChestCheck){
        return lockKey;
      } else {
        return ((LockableContainerBlockEntityMixin) (Object) getLockedEntity()).getKey(true);   // does this actually work????
      }
    }

    @Unique
    public String getKey() {
      return getKey(false);
    }

    @Unique
    public void setAdminLocked(boolean adminLocked) {
      setKey(LockedRimaBlockEntity.adminUUID);
    }

    @Unique
    public boolean isAdminLocked(boolean skipDblChestCheck) {
      return getKey(skipDblChestCheck) == LockedRimaBlockEntity.adminUUID;
    }

    @Unique
    public boolean isAdminLocked() {
      return isAdminLocked(false);
    }

    @Unique
    public boolean isLocked(boolean skipDblChestCheck) {
      return isAdminLocked(skipDblChestCheck) || getKey(skipDblChestCheck) != null;
    }

    @Unique
    public boolean isLocked() {
      return isLocked(false);
    }

    @Unique
    public boolean doesUnlock(String key) {
      return Objects.equals(getKey(), key);
    }

    @Unique
    private LockableContainerBlockEntity getLockedEntity(){    
      var this_ = (LockableContainerBlockEntity) (Object) this; 
      if (lockKey != null){   // already on locked entity. return this
        return this_;
      }
      var blockState = this_.getCachedState();
      if (!blockState.contains(Properties.CHEST_TYPE)){   // no chest type -> no double chest
        return this_;
      }
      var chestside = blockState.get(Properties.CHEST_TYPE);
      if (chestside == ChestType.SINGLE){   // no need to continue if single
        return this_;
      }
      
		  //"facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
      var pos = this_.getPos();
      var offset = (chestside == ChestType.LEFT) ? -1 : 1;  // get positive or negative offset by left or right side
      switch (blockState.get(Properties.HORIZONTAL_FACING)) {    // get position by facing
        case Direction.NORTH:
          pos = pos.west(offset);
          break;
      
        case Direction.EAST:
          pos = pos.north(offset);
          break;
      
        case Direction.SOUTH:
          pos = pos.east(offset);
          break;
      
        case Direction.WEST:
          pos = pos.south(offset);
          break;

        default:
          Rima.LOGGER.warn("Found unknown chest facing at " + pos.toString() + ". This is unhandled behavior! Aborting double chest search.");
          return this_;
        
      }

      if (!(this_.getWorld().getBlockEntity(pos) instanceof LockableContainerBlockEntity newEntity)){   // check if found entity is matching
        Rima.LOGGER.error("Double chest search returned incompatible blockentity at " + pos.toString() + ".");
        return this_; // fallback to previous
      }
      
      if (!((LockableContainerBlockEntityMixin) (Object) newEntity).isLocked(true)){    // only return new entity if locked
        return this_;
      }

      return newEntity;
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.lockKey = null;
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (nbt.contains(LockedRimaBlockEntity.RIMA_KEY_NBT, NbtElement.STRING_TYPE)) {
            this.lockKey = nbt.getString(LockedRimaBlockEntity.RIMA_KEY_NBT);
        } else {
            this.lockKey = null;
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (lockKey != null) {
            nbt.putString(LockedRimaBlockEntity.RIMA_KEY_NBT, lockKey);
        } else {
            nbt.remove(LockedRimaBlockEntity.RIMA_KEY_NBT);
        }
    }

    @Inject(method = "checkUnlocked", at = @At("RETURN"), cancellable = true)
    public void checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            return; // do not handle when default behavior decides entity is locked
        }

        ItemStack heldStack = player.getMainHandStack();
        
        if (isLocked()) {
            if (player.isCreative()
                    || player.isSpectator()
                    || heldStack.isOf(LockItems.ADMIN_KEY_ITEM)
                    || (!isAdminLocked() && canLockpick(player, heldStack))
                    || (!isAdminLocked()
                        && heldStack.isOf(LockItems.KEY_ITEM)
                        && doesUnlock(heldStack.get(RimaDataComponentTypes.RIMA_LOCK)))) { // unlockable
                cir.setReturnValue(true);
            } else {
                player.sendMessage(Text.translatable("message.rima.chest_is_locked"), true);
                cir.setReturnValue(false);
            }
        } else { // chest not locked
            cir.setReturnValue(true);
        }
    }

    @Unique
    public boolean canLockpick(PlayerEntity player, ItemStack heldStack) {
        if (!heldStack.isOf(LockItems.LOCKPICK_ITEM)) {
            return false;
        }

        if (player.getRandom().nextFloat() < 0.05F) {
            return true;
        }

        heldStack.damage(Math.max(1, heldStack.getMaxDamage() / 2), player, EquipmentSlot.MAINHAND);
        return false;
    }

}
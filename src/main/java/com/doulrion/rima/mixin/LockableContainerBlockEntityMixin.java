package com.doulrion.rima.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Properties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.Rima;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.component.RimaLockState;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements ILockableRimaEntity {

    @Unique
    private RimaLockState lockState = new RimaLockState();

    @Unique
    public void setLockState(RimaLockState state, boolean skipDblChestCheck) {
      if (skipDblChestCheck){
        lockState = state;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
      } else {
        ((LockableContainerBlockEntityMixin) (Object) getLockedEntity()).setLockState(state, true);   // does this actually work????
      }
    }   
    
    @Unique
    public void setLockState(RimaLockState state) {
      setLockState(state, false);   // does this actually work????
    }

    @Unique 
    public RimaLockState getLockState(boolean skipDblChestCheck){
      if (skipDblChestCheck){
        return lockState;
      } else {
        return ((LockableContainerBlockEntityMixin) (Object) getLockedEntity()).getLockState(true);   // does this actually work????
      }
    };

    @Unique 
    public RimaLockState getLockState(){
      return getLockState(false);
    };

    @Unique
    private LockableContainerBlockEntity getLockedEntity(){    
      var this_ = (LockableContainerBlockEntity) (Object) this; 
      if (lockState != null && lockState.isLocked()){   // already on locked entity. return this
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
      
      RimaLockState lockState_ = ((LockableContainerBlockEntityMixin) (Object) newEntity).getLockState(true);
      if (!(lockState_ != null && lockState_.isLocked())){    // only return new entity if locked
        return this_;
      }

      return newEntity;
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
      lockState.loadFromEntityNbt(nbt);
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
      lockState.saveToEntityNbt(nbt);
    }


    @Inject(method = "checkUnlocked", at = @At("RETURN"), cancellable = true)

    public void checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
      
      if (!cir.getReturnValue()) {
          return; // do not handle when default behavior decides entity is locked
      }

      cir.setReturnValue(getLockState().checkUnlocked(player));
    }

}
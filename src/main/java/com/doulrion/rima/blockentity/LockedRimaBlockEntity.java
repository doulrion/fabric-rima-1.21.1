package com.doulrion.rima.blockentity;

import com.doulrion.rima.interfaces.ILockableRimaEntity;

import org.jetbrains.annotations.Nullable;

import com.doulrion.rima.component.RimaLockState;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.Packet;

public class LockedRimaBlockEntity extends BlockEntity implements ILockableRimaEntity{
  // private String lockKey = null;
  private RimaLockState lockstate = new RimaLockState();

  public LockedRimaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
  }

  @Override public RimaLockState getLockState(){
    return lockstate;
  };

  @Override public void setLockState(RimaLockState state){
    this.lockstate = state; 
    markDirty();  
    world.updateListeners(this.getPos(), this.getWorld().getBlockState(pos), this.getWorld().getBlockState(pos), 0);
  };

  @Override
  protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
    super.writeNbt(nbt, registries);
    lockstate.saveToEntityNbt(nbt);
  }

  @Override
  public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
    super.readNbt(nbt, registries);
    lockstate.loadFromEntityNbt(nbt);
  }

  @Nullable
  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }
 
  @Override
  public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
    return createNbt(registries);
  }

}
package com.doulrion.rima.blockentity;

import com.doulrion.rima.interfaces.ILockableDoor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class LockedDoorBlockEntity extends BlockEntity implements ILockableDoor {

    private String lockKey = null;
    private boolean adminLocked = false;

    public LockedDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override public void setKey(String key)         { this.lockKey = key; markDirty(); }
    @Override public String getKey()                 { return lockKey; }
    @Override public boolean isLocked()              { return lockKey != null && !lockKey.isEmpty(); }
    @Override public boolean doesUnlock(String key)  { return lockKey != null && lockKey.equals(key); }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (lockKey != null) nbt.putString("rima_lock", lockKey);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        lockKey = nbt.contains("rima_lock") ? nbt.getString("rima_lock") : null;
    }

    @Override
    public void setAdminLocked(boolean adminLocked) {
        this.adminLocked = adminLocked;
        markDirty();
    }

    @Override
    public boolean isAdminLocked() {
        return adminLocked;
    }
}
package com.doulrion.rima.blockentity;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.component.RimaLockState;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.state.property.Properties;
import com.doulrion.rima.component.RimaLockState;

public class LockedRimaBlockEntity extends BlockEntity implements ILockableRimaEntity {
    // private String lockKey = null;
    private RimaLockState lockstate = new RimaLockState();

    public LockedRimaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override public RimaLockState getLockState(){return lockstate;};
    @Override public void setLockState(RimaLockState state){this.lockstate = state; markDirty();};

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
}
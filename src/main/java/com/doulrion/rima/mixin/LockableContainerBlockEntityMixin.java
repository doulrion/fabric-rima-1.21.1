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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.blockentity.LockedRimaBlockEntity;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.item.LockItems;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements ILockableRimaEntity {

    @Unique
    private String lockKey = null;

    // @Unique
    // private boolean rima_admin_lock;

    public void setKey(String key) {
        lockKey = key;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
    }

    public String getKey() {
        return lockKey;
    }

    public void setAdminLocked(boolean adminLocked) {
        lockKey = LockedRimaBlockEntity.adminUUID;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
    }

    public boolean isAdminLocked() {
        return lockKey == LockedRimaBlockEntity.adminUUID;
    }

    // public void doOpen()

    public boolean isLocked() {
        return isAdminLocked() || lockKey != null;
    }

    public boolean doesUnlock(String key) {
        return Objects.equals(lockKey, key);
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
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
 
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableContainerBlockEntity;
import com.doulrion.rima.item.LockItems;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements ILockableContainerBlockEntity {
    private static final String RIMA_KEY_NBT = "rima_key";
    private static final String RIMA_ADMIN_LOCK_NBT = "rima_admin_lock";

    @Unique
    private String rima_key;

    @Unique
    private boolean rima_admin_lock;

    public void setKey(String key) {
        rima_key = key;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
    }

    public String getKey() {
        return rima_key;
    }

    public void setAdminLocked(boolean adminLocked) {
        rima_admin_lock = adminLocked;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
    }

    public boolean isAdminLocked() {
        return rima_admin_lock;
    }

    // public void doOpen()

    public boolean isLocked() {
        return rima_admin_lock || rima_key != null;
    }

    public boolean doesUnlock(String key) {
        return Objects.equals(rima_key, key);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.rima_key = null;
        this.rima_admin_lock = false;
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (nbt.contains(RIMA_KEY_NBT, NbtElement.STRING_TYPE)) {
            this.rima_key = nbt.getString(RIMA_KEY_NBT);
        } else {
            this.rima_key = null;
        }
        this.rima_admin_lock = nbt.getBoolean(RIMA_ADMIN_LOCK_NBT);
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (this.rima_key != null) {
            nbt.putString(RIMA_KEY_NBT, rima_key);
        }
        if (this.rima_admin_lock) {
            nbt.putBoolean(RIMA_ADMIN_LOCK_NBT, true);
        }
    }

    @Inject(method = "checkUnlocked", at = @At("RETURN"))
    public boolean checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            return false; // do not handle when default behavior decides entity is locked
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
                return true;
            }
        } else { // chest not locked
            return true;
        }
        return false;
    }

    @Unique
    private boolean canLockpick(PlayerEntity player, ItemStack heldStack) {
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
package com.doulrion.rima.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.nbt.NbtElement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.IntfLockableContainerBlockEntity;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerBlockEntityMixin implements IntfLockableContainerBlockEntity {
    private static String rima_key_key = "rima_key";

    @Unique
    private String rima_key;

    public void setKey(String key) {
        rima_key = key;
        ((LockableContainerBlockEntity) (Object) this).markDirty();
    }

    public String getKey() {
        return rima_key;
    }

    // public void doOpen()

    public boolean isLocked() {
        return rima_key != null;
    }

    public boolean doesUnlock(String key) {
        return rima_key == key;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.rima_key = null;
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    private void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (nbt.contains(rima_key_key, NbtElement.STRING_TYPE)) {
            this.rima_key = nbt.getString(rima_key_key);
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    private void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        if (this.rima_key != null) {
            nbt.putString(rima_key_key, rima_key);
        }
    }

    @Inject(method = "checkUnlocked", at = @At("RETURN"))
    public boolean checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            return false; // do not handle when default behavior decides entity is locked
        }

        player.sendMessage(Text.literal("Debug: lock_id='" + rima_key + "'"), false); // debug
        if (isLocked()) { // chest locked
            player.sendMessage(Text.literal("Chest is Locked!"), true);
            if (player.isCreative()
                    || player.isSpectator()
                    || player.getMainHandStack().getItem().toString().equals("rima:admin_key")
                    || (player.getMainHandStack().getItem().toString().equals("rima:key")
                        && doesUnlock(player.getMainHandStack().get(RimaDataComponentTypes.RIMA_LOCK)))) { // unlockable
                player.sendMessage(Text.literal("Debug: Chest opened with matching key."), true);
                return true;
            }
        } else { // chest not locked
            player.sendMessage(Text.literal("Debug: Chest is Not Locked!"), true);
            return true;
        }
        return false;
    }

}
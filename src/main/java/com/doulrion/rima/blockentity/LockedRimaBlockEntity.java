package com.doulrion.rima.blockentity;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.doulrion.rima.item.LockItems;

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

public class LockedRimaBlockEntity extends BlockEntity implements ILockableRimaEntity {
    public static final String RIMA_KEY_NBT = "rima_key";
    public static final String adminUUID = "00000000-00000000-00000000-00000000";
    private String lockKey = null;

    public LockedRimaBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override public void setKey(String key)         { this.lockKey = key; markDirty(); }
    @Override public String getKey()                 { return lockKey; }
    @Override public boolean isLocked()              { return lockKey != null && !lockKey.isEmpty();}
    @Override public boolean doesUnlock(String key)  { return lockKey != null && lockKey.equals(key);}
    @Override public boolean isAdminLocked()         { return lockKey != null && lockKey.equals(adminUUID); }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (lockKey != null) {
            nbt.putString(LockedRimaBlockEntity.RIMA_KEY_NBT, lockKey);
        } else {
            nbt.remove(LockedRimaBlockEntity.RIMA_KEY_NBT);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        if (nbt.contains(LockedRimaBlockEntity.RIMA_KEY_NBT, NbtElement.STRING_TYPE)) {
            this.lockKey = nbt.getString(LockedRimaBlockEntity.RIMA_KEY_NBT);
        } else {
            this.lockKey = null;
        }
    }

    @Override
    public void setAdminLocked(boolean adminLocked) {
        this.lockKey = adminLocked ? adminUUID : null;
        markDirty();
    }

    @Override
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

    @Override
    public void HandleOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
      if (world.isClient) return;

      // only handle standard use (even when locked) adding & removing locks is handled in items

      pos = ( state.contains(Properties.DOUBLE_BLOCK_HALF)   // compensate for double blocks (e.g. doors) 
                && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) ? 
            pos.down() : pos;
        
      BlockEntity be = world.getBlockEntity(pos);

      if (!isLocked()) {
          return; // unlocked — let vanilla handle it
      }

      ItemStack held = player.getMainHandStack();

      // Admin key bypasses everything
      if (held.isOf(LockItems.ADMIN_KEY_ITEM) || player.isCreative()) {
          return;
      }

      // Normal key check
      if (held.isOf(LockItems.KEY_ITEM)) {
          String heldId = held.get(RimaDataComponentTypes.RIMA_LOCK);
          if (heldId != null && ((LockedRimaBlockEntity) be).doesUnlock(heldId)) {
              return;     // success
          }
      }

      if (canLockpick(player, held)) {
          return; // success
      }

      player.sendMessage(Text.translatable("message.rima.is_locked"), true);
      cir.setReturnValue(ActionResult.FAIL);
    }
}
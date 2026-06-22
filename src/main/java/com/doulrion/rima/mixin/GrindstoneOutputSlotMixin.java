package com.doulrion.rima.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.doulrion.rima.item.GrindstoneLockHelper;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$4")
public abstract class GrindstoneOutputSlotMixin {
    @Unique
    private boolean rima$wasKeyLockCombination;
    @Unique
    private boolean rima$wasKeyDuplication;
    @Unique
    private boolean rima$wasLockDuplication;
    @Unique
    private boolean rima$wasLockKeyCombination;
    @Unique
    private boolean rima$wasLockIdAssignment;

    @Redirect(
            method = "onTakeItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0))
    private void rima$consumeSlot0(Inventory inventory, int slot, ItemStack stack) {
      // Capture the original input state before any mutation so the second redirect can rely on it.
      ItemStack firstInput = inventory.getStack(0);
      ItemStack secondInput = inventory.getStack(1);
      rima$wasKeyLockCombination = GrindstoneLockHelper.isKeyLockCombination(firstInput, secondInput);
      rima$wasKeyDuplication = GrindstoneLockHelper.isKeyDuplication(firstInput, secondInput);
      rima$wasLockDuplication = GrindstoneLockHelper.isLockDuplication(firstInput, secondInput);
      rima$wasLockKeyCombination = GrindstoneLockHelper.isLockKeyCombination(firstInput, secondInput);
      rima$wasLockIdAssignment = GrindstoneLockHelper.isLockIdAssignment(firstInput, secondInput);

      // Check if this is one of our custom recipes
      if (rima$wasKeyLockCombination || rima$wasKeyDuplication || rima$wasLockDuplication || rima$wasLockKeyCombination || rima$wasLockIdAssignment) {
          // Upper slot never consumed for our recipes
          inventory.setStack(0, inventory.getStack(0));
      } else {
          // Not our recipe, call the original
          inventory.setStack(slot, stack);
      }
    }

    @Redirect(
            method = "onTakeItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1))
    private void rima$consumeSlot1(Inventory inventory, int slot, ItemStack stack) {
      // Check if this is one of our custom recipes
      if (rima$wasKeyLockCombination || rima$wasKeyDuplication || rima$wasLockDuplication || rima$wasLockKeyCombination || rima$wasLockIdAssignment) {
          // Always consume from slot 1 (lower)
          ItemStack slotStack = inventory.getStack(1);
          slotStack.decrement(1);
          inventory.setStack(1, slotStack);
      } else {
          // Not our recipe, call the original
          inventory.setStack(slot, stack);
      }
    }
}
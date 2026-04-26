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
    private boolean rima$slot0WasKey;

    @Redirect(
            method = "onTakeItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0))
    private void rima$consumeOnlyKeySlot0(Inventory inventory, int slot, ItemStack stack) {
        // Capture the original input state before any mutation so the second redirect can rely on it.
        ItemStack firstInput = inventory.getStack(0);
        ItemStack secondInput = inventory.getStack(1);
        rima$wasKeyLockCombination = GrindstoneLockHelper.isKeyLockCombination(firstInput, secondInput);
        rima$slot0WasKey = GrindstoneLockHelper.isKey(firstInput);

        if (!rima$wasKeyLockCombination) {
            inventory.setStack(slot, stack);
            return;
        }

        if (rima$slot0WasKey) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
    }

    @Redirect(
            method = "onTakeItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1))
    private void rima$consumeOnlyKeySlot1(Inventory inventory, int slot, ItemStack stack) {
        // Use the state captured before ordinal=0 mutated the inventory.
        if (!rima$wasKeyLockCombination) {
            inventory.setStack(slot, stack);
            return;
        }

        // Only the key slot should be consumed; slot1 is the key when slot0 was not.
        if (!rima$slot0WasKey) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
    }
}
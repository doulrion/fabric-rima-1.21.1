package com.doulrion.rima.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.doulrion.rima.item.GrindstoneLockHelper;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$4")
public abstract class GrindstoneOutputSlotMixin {
    @Redirect(
            method = "onTakeItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0))
    private void rima$consumeOnlyKeySlot0(Inventory inventory, int slot, ItemStack stack) {
        ItemStack firstInput = inventory.getStack(0);
        ItemStack secondInput = inventory.getStack(1);
        if (!GrindstoneLockHelper.isKeyLockCombination(firstInput, secondInput)) {
            inventory.setStack(slot, stack);
            return;
        }

        if (GrindstoneLockHelper.isKey(firstInput)) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
    }

    @Redirect(
            method = "onTakeItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1))
    private void rima$consumeOnlyKeySlot1(Inventory inventory, int slot, ItemStack stack) {
        ItemStack firstInput = inventory.getStack(0);
        ItemStack secondInput = inventory.getStack(1);
        if (!GrindstoneLockHelper.isKeyLockCombination(firstInput, secondInput)) {
            inventory.setStack(slot, stack);
            return;
        }

        if (GrindstoneLockHelper.isKey(secondInput)) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
    }
}
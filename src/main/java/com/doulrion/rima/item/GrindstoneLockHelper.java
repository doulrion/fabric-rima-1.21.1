package com.doulrion.rima.item;

import com.doulrion.rima.component.RimaDataComponentTypes;

import net.minecraft.item.ItemStack;

public final class GrindstoneLockHelper {
    private GrindstoneLockHelper() {
    }

    public static boolean isGrindstoneInput(ItemStack stack) {
        return stack.isOf(LockItems.KEY_ITEM)
                || stack.isOf(LockItems.LOCK_ITEM);
    }

    public static boolean isKeyLockCombination(ItemStack firstInput, ItemStack secondInput) {
        return !getKeyStack(firstInput, secondInput).isEmpty()
            && !getLockStack(firstInput, secondInput).isEmpty();
    }

    public static ItemStack createKeyResult(ItemStack firstInput, ItemStack secondInput) {
        ItemStack lockStack = getLockStack(firstInput, secondInput);
        if (lockStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        String lockKey = lockStack.get(RimaDataComponentTypes.RIMA_LOCK);
        if (lockKey == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(LockItems.KEY_ITEM);
        result.set(RimaDataComponentTypes.RIMA_LOCK, lockKey);
        return result;
    }

    public static boolean isKey(ItemStack stack) {
        return stack.isOf(LockItems.KEY_ITEM);
    }

    private static ItemStack getKeyStack(ItemStack firstInput, ItemStack secondInput) {
        if (isKey(firstInput)) {
            return firstInput;
        }

        if (isKey(secondInput)) {
            return secondInput;
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getLockStack(ItemStack firstInput, ItemStack secondInput) {
        if (firstInput.isOf(LockItems.LOCK_ITEM)) {
            return firstInput;
        }

        if (secondInput.isOf(LockItems.LOCK_ITEM)) {
            return secondInput;
        }

        return ItemStack.EMPTY;
    }
}
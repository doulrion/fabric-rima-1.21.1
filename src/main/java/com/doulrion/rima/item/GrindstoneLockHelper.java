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
        return firstInput.isOf(LockItems.LOCK_ITEM) && isKey(secondInput);
    }
    public static boolean isLockKeyCombination(ItemStack firstInput, ItemStack secondInput) {
        return isKey(firstInput) && secondInput.isOf(LockItems.LOCK_ITEM);
    }

    public static boolean isKeyDuplication(ItemStack firstInput, ItemStack secondInput) {
        return isKey(firstInput) && isKey(secondInput);
    }

    public static boolean isLockDuplication(ItemStack firstInput, ItemStack secondInput) {
        return firstInput.isOf(LockItems.LOCK_ITEM) && secondInput.isOf(LockItems.LOCK_ITEM);
    }

    public static ItemStack createKeyFromLockResult(ItemStack firstInput) {
        if(!firstInput.isOf(LockItems.LOCK_ITEM))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(LockItems.KEY_ITEM);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static ItemStack createLockFromKeyResult(ItemStack firstInput) {
        if(!isKey(firstInput))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(LockItems.LOCK_ITEM);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static ItemStack createKeyFromKeyResult(ItemStack firstInput) {
        if(!isKey(firstInput))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(LockItems.KEY_ITEM);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static ItemStack createLockFromLockResult(ItemStack firstInput) {
        if(!firstInput.isOf(LockItems.LOCK_ITEM))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(LockItems.LOCK_ITEM);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static boolean isKey(ItemStack stack) {
        return stack.isOf(LockItems.KEY_ITEM);
    }
}
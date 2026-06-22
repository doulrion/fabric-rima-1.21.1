package com.doulrion.rima.item;

import java.util.UUID;

import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.component.RimaHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;


public final class GrindstoneLockHelper {
    private GrindstoneLockHelper() {
    }

    public static boolean isGrindstoneInput(ItemStack stack) {
        return isKey(stack) || isLock(stack);
    }

    public static boolean isKeyLockCombination(ItemStack firstInput, ItemStack secondInput) {
        return isLock(firstInput) && isKey(secondInput);
    }

    public static boolean isLockKeyCombination(ItemStack firstInput, ItemStack secondInput) {
        return isKey(firstInput) && isLock(secondInput); 
    }

    public static boolean isKeyDuplication(ItemStack firstInput, ItemStack secondInput) {
        return isKey(firstInput) && isKey(secondInput);
    }

    public static boolean isLockDuplication(ItemStack firstInput, ItemStack secondInput) {
        return isLock(firstInput) && isLock(secondInput);
    }

    public static boolean isLockIdAssignment(ItemStack firstInput, ItemStack secondInput){
      return isLock(secondInput) && firstInput.isEmpty();
    }

    public static ItemStack createKeyFromLockResult(ItemStack firstInput, Item secondInput) {
        if(!isLock(firstInput))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(secondInput);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static ItemStack createLockFromKeyResult(ItemStack firstInput, Item secondInput) {
        if(!isKey(firstInput))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(secondInput);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static ItemStack createKeyFromKeyResult(ItemStack firstInput, Item secondInput) {
        if(!isKey(firstInput))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(secondInput);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static ItemStack createLockFromIdAssignmentResult(ItemStack firstInput, ItemStack secondInput) {
      if(!isLock(secondInput))
          return ItemStack.EMPTY;

      ItemStack result = new ItemStack(secondInput.getItem());
      result.set(RimaDataComponentTypes.RIMA_LOCK, UUID.randomUUID().toString());
      return result;
    }

    public static ItemStack createLockFromLockResult(ItemStack firstInput, Item secondInput) {
        if(!isLock(firstInput))
            return ItemStack.EMPTY;

        String keyId = firstInput.get(RimaDataComponentTypes.RIMA_LOCK);
        if (keyId == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(secondInput);
        result.set(RimaDataComponentTypes.RIMA_LOCK, keyId);
        return result;
    }

    public static boolean isKey(ItemStack stack) {
        return RimaHelper.isKeyItem(stack) && !stack.isOf(LockItems.ADMIN_KEY_ITEM);
    }    
    public static boolean isLock(ItemStack stack) {
        return RimaHelper.isLockItem(stack) && !stack.isOf(LockItems.ADMIN_LOCK_ITEM);
    }
}
package com.doulrion.rima.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.item.GrindstoneLockHelper;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin {
    @Shadow
    private Inventory input;

    @Inject(method = "getOutputStack", at = @At("HEAD"), cancellable = true)
    private void rima$getOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir) {
        if (GrindstoneLockHelper.isKeyLockCombination(firstInput, secondInput)) {
            cir.setReturnValue(GrindstoneLockHelper.createKeyFromLockResult(firstInput));
        }
        else if (GrindstoneLockHelper.isKeyDuplication(firstInput, secondInput)) {
            cir.setReturnValue(GrindstoneLockHelper.createKeyFromKeyResult(firstInput));
        }
        else if (GrindstoneLockHelper.isLockKeyCombination(firstInput, secondInput)) {
            cir.setReturnValue(GrindstoneLockHelper.createLockFromKeyResult(firstInput));
        }
        else if (GrindstoneLockHelper.isLockDuplication(firstInput, secondInput)) {
            cir.setReturnValue(GrindstoneLockHelper.createLockFromLockResult(firstInput));
        }else{
            return; // continue with vanilla output logic, which will handle non-lock/key inputs as normal
        }
    }
}
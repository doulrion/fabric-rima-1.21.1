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
        if (!GrindstoneLockHelper.isKeyLockCombination(firstInput, secondInput)) {
            return;
        }

        cir.setReturnValue(GrindstoneLockHelper.createKeyResult(firstInput, secondInput));
    }
}
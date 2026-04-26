package com.doulrion.rima.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.doulrion.rima.item.GrindstoneLockHelper;

import net.minecraft.item.ItemStack;

@Mixin(targets = {
        "net.minecraft.screen.GrindstoneScreenHandler$2",
        "net.minecraft.screen.GrindstoneScreenHandler$3"
})
public abstract class GrindstoneInputSlotMixin {
    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void rima$canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (GrindstoneLockHelper.isGrindstoneInput(stack)) {
            cir.setReturnValue(true);
        }
    }
}
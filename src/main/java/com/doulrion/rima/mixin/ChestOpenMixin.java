package com.doulrion.rima.mixin;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public class ChestOpenMixin {
    @Inject(method = "onOpen", at = @At("HEAD"))
    private void onOpen(PlayerEntity player, CallbackInfo ci) {
        if(player.isSpectator() || player.isCreative()) {
            return;
        }
        // Implement Key and Lock logic here
    }
}

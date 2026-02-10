package com.doulrion.rima.mixin;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.doulrion.rima.component.RimaDataComponentTypes;

@Mixin(ChestBlockEntity.class)
public class ChestOpenMixin {
    @Inject(method = "onOpen", at = @At("HEAD"))
    private void onOpen(PlayerEntity player, CallbackInfo ci) {
        
        ChestBlockEntity chest = (ChestBlockEntity)(Object)this;
        NbtCompound blockNbt = chest.createNbt(chest.getWorld().getRegistryManager());

        // Debugging
        player.sendMessage(Text.literal("Debug: " + blockNbt.toString()), false);


        if(player.isSpectator() 
            || player.isCreative()
            || !blockNbt.contains("rima.lock")
            || player.getMainHandStack().getItem().toString().equals("rima:admin_key")) {
            return;
        }

        if( !player.getMainHandStack().getItem().toString().equals("rima:key") ) {
            player.sendMessage(Text.literal("This chest is locked!"), false);
            ci.cancel();
        }

        String keyGUID = player.getMainHandStack().get(RimaDataComponentTypes.RIMA_LOCK);
        String blockLockGUID = blockNbt.getString("rima.lock");

        if( !keyGUID.equals(blockLockGUID) ) {
            player.sendMessage(Text.literal("You don't have the key for this chest!"), false);
            ci.cancel();
        }

        return;

    }
}

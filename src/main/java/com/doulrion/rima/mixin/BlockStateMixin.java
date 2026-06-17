package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import com.mojang.serialization.MapCodec;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.state.property.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockState.class)
public class BlockStateMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec, CallbackInfo ci) {
      RimaBlockEntityTypes.preregisterLockedRimaEntity(block);
    }

}
package com.doulrion.rima.mixin;

import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import com.doulrion.rima.interfaces.ILockableRimaEntity;
import com.mojang.serialization.MapCodec;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.state.property.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockState.class)
public class BlockStateMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec, CallbackInfo ci) {
      // RimaBlockEntityTypes.registerLockedRimaEntity((BlockState) (Object) this);
      // RimaBlockEntityTypes.LOCKED_RIMA_ENTITY.addSupportedBlock(block);
      RimaBlockEntityTypes.preregisterLockedRimaEntity(block);
    }

}
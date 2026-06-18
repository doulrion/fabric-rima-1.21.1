// com/doulrion/rima/blockentity/RimaBlockEntityTypes.java
package com.doulrion.rima.blockentity;


import java.util.ArrayList;

import com.doulrion.rima.Rima;

import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;

public class RimaBlockEntityTypes {

    public static BlockEntityType<LockedRimaBlockEntity> LOCKED_RIMA_ENTITY;
    
    public static ArrayList<Block> preRegistered = new ArrayList<Block>();

    public static Class[] registrable = {LeverBlock.class, ButtonBlock.class, DoorBlock.class, TrapdoorBlock.class};
    

    public static void register() {
 
        var arr = new Block[preRegistered.size()];
        for (var i = 0; i < preRegistered.size(); i++){
          arr[i] = preRegistered.get(i);
        }        
        Rima.LOGGER.info("preRegistering " + preRegistered.size() + " blocks for Generic Lock Entity");

        LOCKED_RIMA_ENTITY = Registry.register(
             
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("rima", "locked_rima_lever"),
            BlockEntityType.Builder.create(
                (pos, state) -> new LockedRimaBlockEntity(LOCKED_RIMA_ENTITY, pos, state), // only assign on runtime
                arr
            ).build()
        );

        arr = null;
    }

    public static void preregisterLockedRimaEntity(Block block){    // horribly inefficient. but it kinda works
      for (Class cl : registrable){
        if (cl.isAssignableFrom(block.getClass())){
          if (preRegistered.contains(block)){
            break;
          }
          preRegistered.add(block);        
          break;
        }
      }
    }

    public static void registerLockedRimaEntity(BlockState state){
      if (!RimaBlockEntityTypes.LOCKED_RIMA_ENTITY.supports(state)){
        RimaBlockEntityTypes.LOCKED_RIMA_ENTITY.addSupportedBlock(state.getBlock());     // dynamically add support for blocks
        Rima.LOGGER.info("added LeverBlockEntity support for block: " + state.getBlock().getName().toString());
      }
    }

    public static BlockEntity registerAndCreateLockedRimaEntity(BlockPos pos, BlockState state) {
      registerLockedRimaEntity(state);
      return new LockedRimaBlockEntity(RimaBlockEntityTypes.LOCKED_RIMA_ENTITY, pos, state);
    }
}
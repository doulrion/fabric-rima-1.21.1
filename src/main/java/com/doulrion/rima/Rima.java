package com.doulrion.rima;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.component.RimaHelper;
import com.doulrion.rima.interfaces.ILockableRimaEntity;

public class Rima implements ModInitializer {
    public static final String MOD_ID = "rima";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Rima has been initialized!");
        RimaDataComponentTypes.registerDataComponentTypes();
        RimaBlockEntityTypes.register();
        LockItems.init();
        registerEvents();
    }

    private void registerEvents() {
      PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
        if (world.getBlockEntity(RimaHelper.normalizeBlockPos(state, pos)) instanceof ILockableRimaEntity rimaEntity 
          && rimaEntity.getLockState().isLocked()) {
            RimaHelper.Messages.messageNotBreakable(player);
            return false; // cancels the break
        }
        return true;
      });
    }
}
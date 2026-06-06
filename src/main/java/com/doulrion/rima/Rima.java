package com.doulrion.rima;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.doulrion.rima.item.LockItems;
import com.doulrion.rima.blockentity.LockedDoorBlockEntity;
import com.doulrion.rima.blockentity.RimaBlockEntityTypes;
import com.doulrion.rima.component.RimaDataComponentTypes;
import com.doulrion.rima.interfaces.ILockableContainerBlockEntity;

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
        RimaBlockEntityTypes.register();
        RimaDataComponentTypes.registerDataComponentTypes();
        LockItems.init();
        registerEvents();
    }

    private void registerEvents() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {

        var blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof DoorBlock) {
            var targetPos = blockState.contains(Properties.DOUBLE_BLOCK_HALF) &&
                    blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER
                    ? pos.down() : pos;
            blockEntity = world.getBlockEntity(targetPos);
        }

        if (blockEntity instanceof LockedDoorBlockEntity door && door.isLocked()) {
            player.sendMessage(Text.translatable("message.rima.door_not_breakable"), true);
            return false; // cancels the break
        }

        if (blockEntity instanceof ILockableContainerBlockEntity container && container.isLocked()) {
            player.sendMessage(Text.translatable("message.rima.chest_not_breakable"), true);
            return false; // cancels the break
         }

        return true;
        });
    }
}
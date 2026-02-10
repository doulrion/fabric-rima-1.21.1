package com.doulrion.rima.component;

import java.util.function.UnaryOperator;

import com.doulrion.rima.Rima;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;

public class RimaDataComponentTypes {

    public static final ComponentType<String> RIMA_LOCK = 
        register("rima.lock", builder -> builder.codec(RimaCodec.INSTANCE) );

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Rima.MOD_ID, name),
            builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void registerDataComponentTypes() {
        Rima.LOGGER.info("Registering Data Component Types for " + Rima.MOD_ID);        
    }
}

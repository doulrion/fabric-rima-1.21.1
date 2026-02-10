package com.doulrion.rima.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class RimaCodec implements Codec<String> {

    @Override
    public <T> DataResult<T> encode(String input, DynamicOps<T> ops, T prefix) {
        
        return DataResult.success(ops.createString(input));
        
    }

    @Override
    public <T> DataResult<Pair<String, T>> decode(DynamicOps<T> ops, T input) {
        
        return ops.getStringValue(input).map(s -> Pair.of(s, ops.empty()));
        
    }

    public static final RimaCodec INSTANCE = new RimaCodec();
    
}

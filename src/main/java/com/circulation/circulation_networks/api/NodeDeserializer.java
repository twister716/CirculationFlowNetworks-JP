package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.INode;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface NodeDeserializer extends Function<CompoundTag, INode> {
    @Override
    @NotNull
    INode apply(CompoundTag tag);
}

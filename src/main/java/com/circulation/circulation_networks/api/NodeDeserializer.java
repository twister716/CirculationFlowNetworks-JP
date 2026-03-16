package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.api.node.INode;
//? if <1.20 {
import net.minecraft.nbt.NBTTagCompound;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
//? if <1.20 {
public interface NodeDeserializer extends Function<NBTTagCompound, INode> {
    @Override
    @NotNull
    INode apply(NBTTagCompound tag);
}
//?} else {
/*public interface NodeDeserializer extends Function<CompoundTag, INode> {

    @Override
    @NotNull
    INode apply(CompoundTag tag);
}
*///?}
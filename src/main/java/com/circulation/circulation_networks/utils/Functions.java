package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.api.NodeCreator;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.api.node.NodeContext;
import com.circulation.circulation_networks.api.node.NodeType;
import com.circulation.circulation_networks.registry.NodeTypes;
//~ mc_imports

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//? if >=1.21 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
*///?}

import org.jetbrains.annotations.NotNull;

public final class Functions {

    @SuppressWarnings("unchecked")
    @NotNull
    public static <N extends INode> N createNode(@org.jetbrains.annotations.NotNull NodeType<? extends N> nodeType, @org.jetbrains.annotations.NotNull NodeContext context) {
        NodeCreator creator = NodeTypes.getCreator(nodeType.id());
        if (creator == null) {
            throw new IllegalArgumentException("No creator registered for node type: " + nodeType.id());
        }
        return (N) creator.apply(context);
    }

    //? if <1.20 {
    @NotNull
    public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
        var nbt = stack.getTagCompound();
        if (nbt == null) {
            stack.setTagCompound(nbt = new NBTTagCompound());
        }
        return nbt;
    }
    //?} else if <1.21 {
    /*@NotNull
    public static CompoundTag getOrCreateTagCompound(ItemStack stack) {
        return stack.getOrCreateTag();
    }
    *///?} else {
    /*@NotNull
    public static CompoundTag getOrCreateTagCompound(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void saveTagCompound(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    *///?}

    public static long mergeChunkCoords(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    public static int getChunkX(BlockPos pos) {
        return pos.getX() >> 4;
    }

    public static int getChunkZ(BlockPos pos) {
        return pos.getZ() >> 4;
    }

    //~ if >=1.20 '(World ' -> '(Level ' {
    public static boolean isChunkLoaded(World world, int chunkX, int chunkZ) {
        //? if <1.20 {
        return world.getChunkProvider().getLoadedChunk(chunkX, chunkZ) != null;
        //?} else {
        /*return world.getChunkSource().hasChunk(chunkX, chunkZ);
        *///?}
    }
    //~}

    //~ if >=1.20 '(World ' -> '(Level ' {
    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return isChunkLoaded(world, getChunkX(pos), getChunkZ(pos));
    }
    //~}

    public static long mergeChunkCoords(BlockPos pos) {
        return mergeChunkCoords(getChunkX(pos), getChunkZ(pos));
    }
}

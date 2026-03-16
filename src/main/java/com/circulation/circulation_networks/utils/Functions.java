package com.circulation.circulation_networks.utils;

//? if <1.20 {
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
*///?}

import javax.annotation.Nonnull;

public final class Functions {

    //? if <1.20 {
    @Nonnull
    public static NBTTagCompound getOrCreateTagCompound(ItemStack stack) {
        var nbt = stack.getTagCompound();
        if (nbt == null) {
            stack.setTagCompound(nbt = new NBTTagCompound());
        }
        return nbt;
    }
    //?} else {
    /*@Nonnull
    public static CompoundTag getOrCreateTagCompound(ItemStack stack) {
        return stack.getOrCreateTag();
    }
    *///?}

    public static long mergeChunkCoords(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    public static long mergeChunkCoords(BlockPos pos) {
        return mergeChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
    }
}

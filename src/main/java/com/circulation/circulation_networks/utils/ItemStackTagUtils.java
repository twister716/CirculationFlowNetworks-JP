package com.circulation.circulation_networks.utils;

//~ mc_imports

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
//? if >=1.21 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
*///?}

import org.jetbrains.annotations.NotNull;

public final class ItemStackTagUtils {

    private ItemStackTagUtils() {
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
}

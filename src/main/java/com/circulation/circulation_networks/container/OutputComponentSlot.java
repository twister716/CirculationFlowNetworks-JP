package com.circulation.circulation_networks.container;

//? if <1.20 {
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
//?} else {
/*import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
*///?}
import org.jetbrains.annotations.NotNull;

public class OutputComponentSlot extends ComponentSlot {

    //? if <1.20 {
    public OutputComponentSlot(IInventory inventory, int index, int relX, int relY) {
    //?} else {
    /*public OutputComponentSlot(Container inventory, int index, int relX, int relY) {
    *///?}
        super(inventory, index, relX, relY);
    }

    @Override
    //? if <1.20 {
    public boolean isItemValid(@NotNull ItemStack stack) {
    //?} else {
    /*public boolean mayPlace(@NotNull ItemStack stack) {
    *///?}
        return false;
    }
}
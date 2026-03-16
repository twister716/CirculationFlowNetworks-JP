package com.circulation.circulation_networks.container;

//? if <1.20 {
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
//?} else {
/*import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
*///?}
import org.jetbrains.annotations.NotNull;

public class FilterComponentSlot extends ComponentSlot {

    private final int maxCount;

    //? if <1.20 {
    public FilterComponentSlot(IInventory inventory, int index, int relX, int relY, int maxCount) {
    //?} else {
    /*public FilterComponentSlot(Container inventory, int index, int relX, int relY, int maxCount) {
    *///?}
        super(inventory, index, relX, relY);
        this.maxCount = maxCount;
    }

    public void ghostClickWith(ItemStack held, int dragType) {
        if (held.isEmpty()) {
            //? if <1.20 {
            putStack(ItemStack.EMPTY);
            //?} else {
            /*set(ItemStack.EMPTY);
            *///?}
        } else {
            ItemStack filter = held.copy();
            //? if <1.20 {
            filter.setCount(dragType == 1 ? 1 : Math.min(held.getCount(), getSlotStackLimit()));
            putStack(filter);
            //?} else {
            /*filter.setCount(dragType == 1 ? 1 : Math.min(held.getCount(), getMaxStackSize()));
            set(filter);
            *///?}
        }
    }

    @Override
    //? if <1.20 {
    public void putStack(ItemStack is) {
        super.putStack(is.isEmpty() ? is : is.copy());
    }
    //?} else {
    /*public void set(ItemStack is) {
        super.set(is.isEmpty() ? is : is.copy());
    }
    *///?}

    @Override
    //? if <1.20 {
    public @NotNull ItemStack decrStackSize(int amount) {
    //?} else {
    /*public @NotNull ItemStack remove(int amount) {
    *///?}
        return ItemStack.EMPTY;
    }

    //? if <1.20 {
    @Override
    public @NotNull ItemStack onTake(@NotNull EntityPlayer player, @NotNull ItemStack stack) {
        return stack;
    }
    //?} else {
    /*@Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
    }
    *///?}

    @Override
    //? if <1.20 {
    public boolean isItemValid(@NotNull ItemStack stack) {
    //?} else {
    /*public boolean mayPlace(@NotNull ItemStack stack) {
    *///?}
        return false;
    }

    //? if <1.20 {
    @Override
    public boolean canTakeStack(@NotNull EntityPlayer player) {
    //?} else {
    /*@Override
    public boolean mayPickup(@NotNull Player player) {
    *///?}
        return false;
    }

    @Override
    //? if <1.20 {
    public int getSlotStackLimit() {
    //?} else {
    /*public int getMaxStackSize() {
    *///?}
        return maxCount;
    }
}
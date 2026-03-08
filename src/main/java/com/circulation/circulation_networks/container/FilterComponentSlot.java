package com.circulation.circulation_networks.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilterComponentSlot extends ComponentSlot {

    private final int maxCount;

    public FilterComponentSlot(IInventory inventory, int index, int relX, int relY, int maxCount) {
        super(inventory, index, relX, relY);
        this.maxCount = maxCount;
    }

    public void ghostClickWith(ItemStack held, int dragType) {
        if (held.isEmpty()) {
            putStack(ItemStack.EMPTY);
        } else {
            ItemStack filter = held.copy();
            filter.setCount(dragType == 1 ? 1 : Math.min(held.getCount(), getSlotStackLimit()));
            putStack(filter);
        }
    }

    @Override
    public void putStack(ItemStack is) {
        super.putStack(is.isEmpty() ? is : is.copy());
    }

    @Override
    public @NotNull ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack onTake(@NotNull EntityPlayer player, @NotNull ItemStack stack) {
        return stack;
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(@NotNull EntityPlayer player) {
        return false;
    }

    @Override
    public int getSlotStackLimit() {
        return maxCount;
    }
}

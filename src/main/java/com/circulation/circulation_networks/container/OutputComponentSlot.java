package com.circulation.circulation_networks.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OutputComponentSlot extends ComponentSlot {

    public OutputComponentSlot(IInventory inventory, int index, int relX, int relY) {
        super(inventory, index, relX, relY);
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        return false;
    }
}

package com.circulation.circulation_networks.container;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OutputComponentSlot extends ComponentSlot {

    public OutputComponentSlot(Container inventory, int index, int relX, int relY) {
        super(inventory, index, relX, relY);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }
}

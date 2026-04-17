package com.circulation.circulation_networks.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilterComponentSlot extends ComponentSlot {

    private final int maxCount;

    public FilterComponentSlot(Container inventory, int index, int relX, int relY, int maxCount) {
        super(inventory, index, relX, relY);
        this.maxCount = maxCount;
    }

    public void ghostClickWith(ItemStack held, int dragType) {
        if (held.isEmpty()) {
            set(ItemStack.EMPTY);
        } else {
            ItemStack filter = held.copy();
            filter.setCount(dragType == 1 ? 1 : Math.min(held.getCount(), getMaxStackSize()));
            set(filter);
        }
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack.isEmpty() ? stack : stack.copy());
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return maxCount;
    }
}

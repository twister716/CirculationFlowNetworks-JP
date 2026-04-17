package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.inventory.CFNInternalInventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class ComponentSlot extends Slot {

    private final int relX;
    private final int relY;
    private boolean visible = true;

    public ComponentSlot(Container inventory, int index, int relX, int relY) {
        super(inventory, index, relX, relY);
        this.relX = relX;
        this.relY = relY;
    }

    public int getRelX() {
        return relX;
    }

    public int getRelY() {
        return relY;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int getMaxStackSize() {
        if (container instanceof CFNInternalInventory inventory) {
            return inventory.getSlotLimit(getSlotIndex());
        }
        return super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(@NonNull ItemStack stack) {
        if (container instanceof CFNInternalInventory inventory) {
            return Math.min(inventory.getSlotLimit(getSlotIndex()), stack.getMaxStackSize());
        }
        return super.getMaxStackSize(stack);
    }

    @Override
    public boolean isActive() {
        return visible;
    }
}

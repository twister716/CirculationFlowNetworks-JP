package com.circulation.circulation_networks.container;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

@Getter
public class ComponentSlot extends Slot {

    private final int relX;
    private final int relY;
    @Setter
    private boolean visible = true;

    public ComponentSlot(IInventory inventory, int index, int relX, int relY) {
        super(inventory, index, relX, relY);
        this.relX = relX;
        this.relY = relY;
    }

    @Override
    public boolean isEnabled() {
        return visible;
    }

}

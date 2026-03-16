package com.circulation.circulation_networks.container;

//? if <1.20 {
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
//?} else {
/*import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
*///?}

public class ComponentSlot extends Slot {

    private final int relX;
    private final int relY;
    private boolean visible = true;

    //? if <1.20 {
    public ComponentSlot(IInventory inventory, int index, int relX, int relY) {
    //?} else {
    /*public ComponentSlot(Container inventory, int index, int relX, int relY) {
    *///?}
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    //? if <1.20 {
    public boolean isEnabled() {
    //?} else {
    /*public boolean isActive() {
    *///?}
        return visible;
    }
}
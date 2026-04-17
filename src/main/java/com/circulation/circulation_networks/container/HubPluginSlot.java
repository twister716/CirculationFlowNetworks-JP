package com.circulation.circulation_networks.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class HubPluginSlot extends ComponentSlot {

    private final BooleanSupplier canModifySupplier;

    public HubPluginSlot(BooleanSupplier canModifySupplier, Container inventory, int index, int relX, int relY) {
        super(inventory, index, relX, relY);
        this.canModifySupplier = canModifySupplier;
    }

    public boolean canModify() {
        return canModifySupplier.getAsBoolean();
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return canModify();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return canModify() && super.mayPlace(stack);
    }
}

package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.api.node.IHubNode;
//~ mc_imports
import net.minecraft.item.ItemStack;
//? if <1.20 {
import net.minecraft.entity.player.EntityPlayer;
//?} else {
/*import net.minecraft.world.entity.player.Player;
 *///?}
//? if <1.21 {
import net.minecraftforge.items.IItemHandler;
//?} else {
/*import net.neoforged.neoforge.items.IItemHandler;
 *///?}
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HubPluginSlot extends ComponentSlot {

    private final IHubNode node;
    private final UUID playerId;

    public HubPluginSlot(IHubNode node, UUID playerId, IItemHandler inventory, int index, int relX, int relY) {
        super(inventory, index, relX, relY);
        this.node = node;
        this.playerId = playerId;
    }

    public boolean canModify() {
        return node.canEditPermissions(playerId);
    }

    //~ if >=1.20 'canTakeStack(@NotNull EntityPlayer' -> 'mayPickup(@NotNull Player' {
    //~ if >=1.20 'isItemValid(' -> 'mayPlace(' {
    @Override
    public boolean canTakeStack(@NotNull EntityPlayer player) {
        return canModify();
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        return canModify() && super.isItemValid(stack);
    }
    //~}
    //~}
}

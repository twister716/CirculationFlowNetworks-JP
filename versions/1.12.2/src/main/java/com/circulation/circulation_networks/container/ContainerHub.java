package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.manager.EnergyMachineManager;
import com.circulation.circulation_networks.tiles.nodes.TileEntityHub;
import com.circulation.circulation_networks.utils.GuiSync;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class ContainerHub extends CFNBaseContainer {

    public final ComponentSlotLayout playerInvLayout;
    private final TileEntityHub te;
    @GuiSync(0)
    public String input;
    @GuiSync(1)
    public String output;

    public ContainerHub(EntityPlayer player, TileEntityHub te) {
        super(player);
        this.te = te;

        playerInvLayout = registerPlayerLayout(ComponentSlotLayout.playerInventory(player.inventory));
        if (te.getWorld().isRemote) {
            input = "0";
            output = "0";
        } else {
            var energy = EnergyMachineManager.INSTANCE.getInteraction().get(te.getNode().getGrid());
            input = energy.getInput().toString();
            output = energy.getOutput().toString();
        }
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return playerIn.getDistanceSq(te.getPos().getX() + 0.5D, te.getPos().getY() + 0.5D, te.getPos().getZ() + 0.5D) <= 128;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (te.getWorld().isRemote || te.getWorld().getTotalWorldTime() % 10 != 0) return;
        var energy = EnergyMachineManager.INSTANCE.getInteraction().get(te.getNode().getGrid());
        input = energy.getInput().toString();
        output = energy.getOutput().toString();
    }
}

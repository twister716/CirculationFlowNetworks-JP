package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.tiles.TileEntityCirculationShielder;
import com.circulation.circulation_networks.utils.GuiSync;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCirculationShielder extends CFNBaseContainer {

    public final TileEntityCirculationShielder te;
    @GuiSync(0)
    public int scope;
    @GuiSync(1)
    public int maxScope;

    public ContainerCirculationShielder(EntityPlayer player, TileEntityCirculationShielder te) {
        super(player);
        this.te = te;
        this.maxScope = te.getMaxScope();
    }

    @Override
    public void detectAndSendChanges() {
        scope = te.getScope();
        maxScope = te.getMaxScope();
        super.detectAndSendChanges();
    }
}

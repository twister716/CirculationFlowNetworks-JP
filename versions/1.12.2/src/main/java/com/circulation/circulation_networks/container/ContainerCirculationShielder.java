package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.gui.GuiCirculationShielder;
import com.circulation.circulation_networks.tiles.TileEntityCirculationShielder;
import com.circulation.circulation_networks.utils.GuiSync;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCirculationShielder extends CFNBaseContainer {

    public final TileEntityCirculationShielder te;
    @GuiSync(0)
    public int scope;

    public ContainerCirculationShielder(EntityPlayer player, TileEntityCirculationShielder te) {
        super(player);
        this.te = te;
    }

    @Override
    public void detectAndSendChanges() {
        scope = te.getScope();
    }

    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        if (this.te.getWorld().isRemote) {
            if ("scope".equals(field)) {
                onCilent((Integer) newValue);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onCilent(Integer v) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiCirculationShielder g) {
            g.scopeField.setText(String.valueOf(v));
        }
    }
}

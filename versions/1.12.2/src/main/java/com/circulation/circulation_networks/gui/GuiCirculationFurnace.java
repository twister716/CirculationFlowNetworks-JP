package com.circulation.circulation_networks.gui;

import com.circulation.circulation_networks.container.ContainerCirculationFurnace;
import com.circulation.circulation_networks.gui.component.InventoryComponent;
import com.circulation.circulation_networks.gui.component.SlotComponent;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.tiles.machines.TileEntityCirculationFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiCirculationFurnace extends CFNBaseGui {

    private final ContainerCirculationFurnace container;

    public GuiCirculationFurnace(EntityPlayer player, TileEntityCirculationFurnace te) {
        super(new ContainerCirculationFurnace(player, te));
        this.container = (ContainerCirculationFurnace) inventorySlots;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected List<Component> buildComponents(List<Component> components) {
        components.add(new SlotComponent(56, 35, container.inputLayout, "slot", this));
        components.add(new SlotComponent(116, 35, container.outputLayout, "slot", this));
        components.add(new InventoryComponent(8, 84, container.playerInvLayout, this));
        return components;
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        // GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        // this.mc.getTextureManager().bindTexture(TEXTURE);
        // this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
    }
}

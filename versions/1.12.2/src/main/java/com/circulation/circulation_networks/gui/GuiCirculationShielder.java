package com.circulation.circulation_networks.gui;

import com.circulation.circulation_networks.container.ContainerCirculationShielder;
import com.circulation.circulation_networks.gui.component.BackgroundComponent;
import com.circulation.circulation_networks.gui.component.CirculationShielderPanelComponent;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.RenderPhase;
import com.circulation.circulation_networks.tiles.TileEntityCirculationShielder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiCirculationShielder extends CFNBaseGui<ContainerCirculationShielder> {

    private static final int GUI_WIDTH = 138;
    private static final int GUI_HEIGHT = 56;

    private final TileEntityCirculationShielder tileEntity;

    public GuiCirculationShielder(EntityPlayer player, TileEntityCirculationShielder te) {
        super(new ContainerCirculationShielder(player, te));
        this.tileEntity = te;
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;
    }

    @Override
    protected void buildComponents(Map<RenderPhase, List<Component>> components) {
        List<Component> bg = components.computeIfAbsent(RenderPhase.BACKGROUND, k -> new ObjectArrayList<>());
        bg.add(new BackgroundComponent("shielder_base", this));
        List<Component> normal = components.computeIfAbsent(RenderPhase.NORMAL, k -> new ObjectArrayList<>());
        normal.add(new CirculationShielderPanelComponent(container, tileEntity, this));
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
    }
}

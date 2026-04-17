package com.circulation.circulation_networks.gui;

import com.circulation.circulation_networks.container.ContainerCirculationShielder;
import com.circulation.circulation_networks.gui.component.BackgroundComponent;
import com.circulation.circulation_networks.gui.component.CirculationShielderPanelComponent;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.RenderPhase;
import com.circulation.circulation_networks.tiles.BlockEntityCirculationShielder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Map;

public class GuiCirculationShielder extends CFNBaseGui<ContainerCirculationShielder> {

    private static final int GUI_WIDTH = 138;
    private static final int GUI_HEIGHT = 56;

    private final BlockEntityCirculationShielder tileEntity;

    public GuiCirculationShielder(ContainerCirculationShielder container, Inventory playerInventory, net.minecraft.network.chat.Component title) {
        super(container, playerInventory, title, GUI_WIDTH, GUI_HEIGHT);
        this.tileEntity = container.te;
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

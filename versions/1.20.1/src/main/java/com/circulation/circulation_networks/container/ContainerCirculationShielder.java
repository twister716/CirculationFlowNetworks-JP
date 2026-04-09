package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.tiles.CirculationShielderBlockEntity;
import com.circulation.circulation_networks.utils.GuiSync;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ContainerCirculationShielder extends CFNBaseContainer {

    public final CirculationShielderBlockEntity te;
    @GuiSync(0)
    public int scope;
    @GuiSync(1)
    public int maxScope;

    public ContainerCirculationShielder(MenuType<?> menuType, int containerId, Player player, CirculationShielderBlockEntity te) {
        super(menuType, containerId, player);
        this.te = te;
        this.maxScope = te.getMaxScope();
    }

    @Override
    public void broadcastChanges() {
        scope = te.getScope();
        maxScope = te.getMaxScope();
        super.broadcastChanges();
    }
}

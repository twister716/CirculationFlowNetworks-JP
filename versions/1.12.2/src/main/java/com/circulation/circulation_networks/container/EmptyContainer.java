package com.circulation.circulation_networks.container;

import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public final class EmptyContainer extends CFNBaseContainer {

    public EmptyContainer() {
        super(null, null);
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return false;
    }

    public void detectAndSendChanges() {

    }
}
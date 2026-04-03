package com.circulation.circulation_networks.pocket;

import com.circulation.circulation_networks.api.ClientTickMachine;
import com.circulation.circulation_networks.registry.PocketNodeItems;
//~ mc_imports
import net.minecraft.item.ItemStack;

public final class PocketNodeClientHost implements ClientTickMachine {

    private final PocketNodeRecord record;
    private final ItemStack renderStack;

    public PocketNodeClientHost(PocketNodeRecord record) {
        this.record = record;
        this.renderStack = PocketNodeItems.createStack(record.nodeType());
    }

    public PocketNodeRecord getRecord() {
        return record;
    }

    public ItemStack getRenderStack() {
        return renderStack;
    }

    @Override
    public void clientUpdate() {
    }
}
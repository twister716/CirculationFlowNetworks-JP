package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.api.node.NodeType;
//~ mc_imports
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class PocketNodeItems {

    private PocketNodeItems() {
    }

    public static @Nullable Item getItemForType(NodeType<?> nodeType) {
        return null;
    }

    public static ItemStack createStack(NodeType<?> nodeType) {
        Item item = getItemForType(nodeType);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}
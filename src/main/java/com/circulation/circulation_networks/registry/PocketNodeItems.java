package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.api.node.NodeType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
//~ mc_imports
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class PocketNodeItems {

    private static final Object2ReferenceMap<String, Item> ITEMS_BY_NODE_TYPE_ID = new Object2ReferenceOpenHashMap<>();

    private PocketNodeItems() {
    }

    public static void register(NodeType<?> nodeType, Item item) {
        ITEMS_BY_NODE_TYPE_ID.put(nodeType.id(), item);
    }

    public static @Nullable Item getItemForType(NodeType<?> nodeType) {
        return ITEMS_BY_NODE_TYPE_ID.get(nodeType.id());
    }

    public static ItemStack createStack(NodeType<?> nodeType) {
        Item item = getItemForType(nodeType);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}

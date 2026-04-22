package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.api.node.NodeType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class PocketNodeItems {

    private static final Object2ReferenceMap<String, Item> ITEMS_BY_NODE_TYPE_ID = new Object2ReferenceOpenHashMap<>();

    private PocketNodeItems() {
    }

    public static void register(NodeType<?> nodeType, Item item) {
        ITEMS_BY_NODE_TYPE_ID.put(nodeType.id(), item);
    }

    public static Item getItemForType(NodeType<?> nodeType) {
        Item pocketItem = ITEMS_BY_NODE_TYPE_ID.get(nodeType.id());
        if (pocketItem != null) {
            return pocketItem;
        }
        throw new IllegalStateException("Pocket node item is not registered for node type: " + nodeType.id());
    }

    public static ItemStack createStack(NodeType<?> nodeType) {
        return new ItemStack(getItemForType(nodeType));
    }
}

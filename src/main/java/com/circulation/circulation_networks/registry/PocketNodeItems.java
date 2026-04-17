package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.api.node.NodeType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public final class PocketNodeItems {

    private static final Object2ReferenceMap<String, Item> ITEMS_BY_NODE_TYPE_ID = new Object2ReferenceOpenHashMap<>();

    private PocketNodeItems() {
    }

    public static void register(NodeType<?> nodeType, Item item) {
        ITEMS_BY_NODE_TYPE_ID.put(nodeType.id(), item);
    }

    public static @Nullable Item getItemForType(NodeType<?> nodeType) {
        Item pocketItem = ITEMS_BY_NODE_TYPE_ID.get(nodeType.id());
        if (pocketItem != null) {
            return pocketItem;
        }
        String visualId = nodeType.fallbackVisualId();
        if (visualId == null || visualId.isEmpty()) {
            return null;
        }
        Identifier location = Identifier.parse(visualId);
        var blockHolder = BuiltInRegistries.BLOCK.get(location).orElse(null);
        Block block = blockHolder != null ? blockHolder.value() : null;
        Item blockItem = block != null ? block.asItem() : net.minecraft.world.item.Items.AIR;
        return blockItem != net.minecraft.world.item.Items.AIR ? blockItem : null;
    }

    public static ItemStack createStack(NodeType<?> nodeType) {
        Item item = getItemForType(nodeType);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}

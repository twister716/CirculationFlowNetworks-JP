package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.api.node.NodeType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
//~ mc_imports
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//? if <1.20 {
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
//?} else {
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ResourceLocation;
*///?}
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
        //? if <1.20 {
        ResourceLocation location = new ResourceLocation(visualId);
        Block block = Block.REGISTRY.getObject(location);
        if (block == null) {
            return null;
        }
        return Item.getItemFromBlock(block);
        //?} else if <1.21 {
        /*ResourceLocation location = new ResourceLocation(visualId);
        var block = BuiltInRegistries.BLOCK.get(location);
        Item blockItem = Item.byBlock(block);
        return blockItem != net.minecraft.world.item.Items.AIR ? blockItem : null;
        *///?} else {
        /*ResourceLocation location = ResourceLocation.parse(visualId);
        var block = BuiltInRegistries.BLOCK.get(location);
        Item blockItem = Item.byBlock(block);
        return blockItem != net.minecraft.world.item.Items.AIR ? blockItem : null;
        *///?}
    }

    public static ItemStack createStack(NodeType<?> nodeType) {
        Item item = getItemForType(nodeType);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}

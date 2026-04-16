package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.api.node.NodeType;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BuiltinPocketNodeItem extends ItemPocketNode {

    public BuiltinPocketNodeItem(NodeType<?> nodeType, Properties properties) {
        super(nodeType, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return com.circulation.circulation_networks.client.render.PocketNodeItemStackRenderer.getInstance();
            }
        });
    }
}

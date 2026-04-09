package com.circulation.circulation_networks.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AnimatedNodeBlockItem extends BlockItem {

    public AnimatedNodeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.@NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return com.circulation.circulation_networks.client.render.AnimatedNodeItemStackRenderer.getInstance();
            }
        });
    }
}

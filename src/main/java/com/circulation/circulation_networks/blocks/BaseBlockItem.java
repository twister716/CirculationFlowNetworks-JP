package com.circulation.circulation_networks.blocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class BaseBlockItem extends BlockItem {

    public BaseBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        if (getBlock() instanceof BaseBlock baseBlock) {
            for (var lc : baseBlock.buildTooltips(stack)) {
                builder.accept(Component.literal(lc.get()));
            }
        }
    }
}

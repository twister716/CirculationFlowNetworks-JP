package com.circulation.circulation_networks.blocks;

import com.circulation.circulation_networks.items.BaseItemTooltipModel;
import com.circulation.circulation_networks.utils.CI18n;
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
    private final boolean moveFirstStaticTooltipToEnd;

    public BaseBlockItem(Block block, Properties properties) {
        this(block, properties, false);
    }

    public BaseBlockItem(Block block, Properties properties, boolean moveFirstStaticTooltipToEnd) {
        super(block, properties);
        this.moveFirstStaticTooltipToEnd = moveFirstStaticTooltipToEnd;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        String[] tooltipKeys = BaseItemTooltipModel.resolveTooltipKeys(this.getDescriptionId(), CI18n::hasKey);
        if (moveFirstStaticTooltipToEnd) {
            tooltipKeys = BaseItemTooltipModel.moveFirstTooltipKeyToEnd(tooltipKeys);
        }
        for (String key : tooltipKeys) {
            builder.accept(Component.translatable(key));
        }
        if (getBlock() instanceof BaseBlock baseBlock) {
            for (var lc : baseBlock.buildTooltips(stack)) {
                builder.accept(Component.literal(lc.get()));
            }
        }
    }
}

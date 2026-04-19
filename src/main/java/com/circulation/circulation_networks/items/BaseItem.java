package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.tooltip.LocalizedComponent;
import com.circulation.circulation_networks.utils.CI18n;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public abstract class BaseItem extends Item {

    protected BaseItem(Properties properties) {
        super(properties);
    }

    protected List<LocalizedComponent> buildTooltips(ItemStack stack) {
        return new ObjectArrayList<>();
    }

    protected String getStaticTooltipBaseKey(ItemStack stack) {
        return this.getDescriptionId();
    }

    protected boolean moveFirstStaticTooltipToEnd(ItemStack stack) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                      @NotNull TooltipDisplay display,
                                      @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        String[] tooltipKeys = BaseItemTooltipModel.resolveTooltipKeys(getStaticTooltipBaseKey(stack), CI18n::hasKey);
        if (moveFirstStaticTooltipToEnd(stack)) {
            tooltipKeys = BaseItemTooltipModel.moveFirstTooltipKeyToEnd(tooltipKeys);
        }
        for (String key : tooltipKeys) {
            tooltip.accept(Component.translatable(key));
        }
        for (var lc : buildTooltips(stack)) {
            tooltip.accept(Component.literal(lc.get()));
        }
    }
}

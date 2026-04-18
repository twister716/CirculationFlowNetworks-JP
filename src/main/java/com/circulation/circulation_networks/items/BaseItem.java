package com.circulation.circulation_networks.items;

import com.circulation.circulation_networks.tooltip.LocalizedComponent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import com.circulation.circulation_networks.registry.CFNDataComponents;
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

    @Override
    @SuppressWarnings("deprecation")
    public final void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                      @NotNull TooltipDisplay display,
                                      @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        stack.addToTooltip(CFNDataComponents.TOOLTIP_TRANSLATIONS, context, display, tooltip, flag);
        for (var lc : buildTooltips(stack)) {
            tooltip.accept(Component.literal(lc.get()));
        }
    }
}

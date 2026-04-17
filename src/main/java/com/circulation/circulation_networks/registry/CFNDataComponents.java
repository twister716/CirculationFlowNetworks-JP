package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.tooltip.TooltipTranslationsComponent;
import net.minecraft.core.component.DataComponentType;

public final class CFNDataComponents {

    public static final DataComponentType<TooltipTranslationsComponent> TOOLTIP_TRANSLATIONS =
        DataComponentType.<TooltipTranslationsComponent>builder()
                         .persistent(TooltipTranslationsComponent.CODEC)
                         .cacheEncoding()
                         .build();

    private CFNDataComponents() {
    }
}

package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class RegistryDataComponents {

    private RegistryDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegistryDataComponents::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE, helper ->
            helper.register(
                Identifier.parse(CirculationFlowNetworks.MOD_ID + ":tooltip_translations"),
                CFNDataComponents.TOOLTIP_TRANSLATIONS
            )
        );
    }
}

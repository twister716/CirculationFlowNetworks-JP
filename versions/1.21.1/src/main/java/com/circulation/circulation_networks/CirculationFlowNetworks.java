package com.circulation.circulation_networks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CirculationFlowNetworks.MOD_ID)
public final class CirculationFlowNetworks {

    public static final String MOD_ID = "circulation_networks";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void sendToPlayer(Object packet, Object player) {
        throw new UnsupportedOperationException("Packet sending is not implemented for 1.21.1 yet");
    }

    public static void sendToServer(Object packet) {
        throw new UnsupportedOperationException("Packet sending is not implemented for 1.21.1 yet");
    }

    public CirculationFlowNetworks(IEventBus modEventBus, ModContainer modContainer) {
        CFNConfig.register(modContainer);
        modEventBus.addListener(CFNConfig::onConfigLoad);
        modEventBus.addListener(CFNConfig::onConfigReload);
    }
}
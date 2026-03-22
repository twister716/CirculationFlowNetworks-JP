package com.circulation.circulation_networks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CirculationFlowNetworks.MOD_ID)
public final class CirculationFlowNetworks {

    public static final String MOD_ID = "circulation_networks";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void sendToPlayer(Object packet, Object player) {
        throw new UnsupportedOperationException("Packet sending is not implemented for 1.20.1 yet");
    }

    public static void sendToServer(Object packet) {
        throw new UnsupportedOperationException("Packet sending is not implemented for 1.20.1 yet");
    }

    public CirculationFlowNetworks() {
        CFNConfig.register();
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CFNConfig::onConfigLoad);
        modEventBus.addListener(CFNConfig::onConfigReload);
    }
}
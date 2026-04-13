package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.events.BlockEntityLifeCycleEvent;
import com.circulation.circulation_networks.manager.EnergyMachineManager;
import com.circulation.circulation_networks.manager.EnergyTypeOverrideManager;
import com.circulation.circulation_networks.manager.HubChannelManager;
import com.circulation.circulation_networks.manager.MachineNodeBlockEntityManager;
import com.circulation.circulation_networks.manager.NetworkManager;

public final class BlockEntityLifecycleDispatcher {

    private BlockEntityLifecycleDispatcher() {
    }

    public static void onValidate(BlockEntityLifeCycleEvent.Validate event) {
        MachineNodeBlockEntityManager.INSTANCE.onBlockEntityValidate(event);
        NetworkManager.INSTANCE.onBlockEntityValidate(event);
        EnergyMachineManager.INSTANCE.onBlockEntityValidate(event);
        HubChannelManager.INSTANCE.onBlockEntityValidate(event);
    }

    public static void onInvalidate(BlockEntityLifeCycleEvent.Invalidate event) {
        MachineNodeBlockEntityManager.INSTANCE.onBlockEntityInvalidate(event);
        NetworkManager.INSTANCE.onBlockEntityInvalidate(event);
        EnergyMachineManager.INSTANCE.onBlockEntityInvalidate(event);
        HubChannelManager.INSTANCE.onBlockEntityInvalidate(event);
        var overrideManager = EnergyTypeOverrideManager.get();
        if (overrideManager != null) {
            overrideManager.onBlockEntityInvalidate(event);
        }
    }
}

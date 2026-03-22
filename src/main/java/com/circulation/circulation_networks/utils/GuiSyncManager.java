package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.lang.reflect.Field;

public class GuiSyncManager {
    private final Int2ObjectMap<SyncData> syncData = new Int2ObjectOpenHashMap<>();

    public void init() {
        for (var value : this.syncData.values()) {
             value.init();
        }
    }

    public void scan(Object container, SyncData.SyncUpdateCallback updateCallback) {
        for (Field f : container.getClass().getFields()) {
            if (f.isAnnotationPresent(GuiSync.class)) {
                GuiSync annotation = f.getAnnotation(GuiSync.class);
                if (this.syncData.containsKey(annotation.value())) {
                    CirculationFlowNetworks.LOGGER.warn("Channel already in use: {} for {}", annotation.value(), f.getName());
                } else {
                    this.syncData.put(annotation.value(), new SyncData(container, f, annotation, updateCallback));
                }
            }
        }
    }

    public void detectAndSendChanges(SyncSender sender) {
        for (SyncData sd : this.syncData.values()) {
            sd.tick(sender);
        }
    }

    public void updateField(int channel, Object value) {
        if (this.syncData.containsKey(channel)) {
            this.syncData.get(channel).update(value);
        }
    }

    public boolean hasChannel(int channel) {
        return this.syncData.containsKey(channel);
    }
}

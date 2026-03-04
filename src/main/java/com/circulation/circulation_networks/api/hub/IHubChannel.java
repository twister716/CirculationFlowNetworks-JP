package com.circulation.circulation_networks.api.hub;

import com.circulation.circulation_networks.api.IGrid;
import it.unimi.dsi.fastutil.objects.ReferenceSet;

import java.util.UUID;

/**
 * 中枢频道接口，同频道的网络在能量处理时会被合并处理
 * <p>
 * Hub channel interface. Networks on the same channel will be merged during energy processing.
 */
public interface IHubChannel {

    UUID getChannelId();

    ReferenceSet<IGrid> getGrids();

    String getName();

    void setName(String name);

    PermissionMode getPermissionMode();

    void setPermissionMode(PermissionMode mode);
}

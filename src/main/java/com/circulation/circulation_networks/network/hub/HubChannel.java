package com.circulation.circulation_networks.network.hub;

import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.hub.IHubChannel;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class HubChannel implements IHubChannel {

    @Getter
    private final UUID channelId;
    @Getter
    private final ReferenceSet<IGrid> grids = new ReferenceOpenHashSet<>();
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private PermissionMode permissionMode;

    public HubChannel(UUID channelId, EntityPlayerMP player, PermissionMode permissionMode) {
        this(channelId, player.getName(),permissionMode);
    }

    public HubChannel(UUID channelId, String name,PermissionMode permissionMode) {
        this.channelId = channelId;
        this.name = name;
        this.permissionMode = permissionMode;
    }

    public void addGrid(IGrid grid) {
        grids.add(grid);
    }

    public void removeGrid(IGrid grid) {
        grids.remove(grid);
    }
}

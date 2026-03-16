package com.circulation.circulation_networks.network.hub;

import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.hub.IHubChannel;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
//? if <1.20 {
import net.minecraft.entity.player.EntityPlayerMP;
//?} else {
/*import net.minecraft.server.level.ServerPlayer;
*///?}

import java.util.UUID;

public class HubChannel implements IHubChannel {

    private final UUID channelId;
    private final ReferenceSet<IGrid> grids = new ReferenceOpenHashSet<>();
    private String name;
    private PermissionMode permissionMode;

    //? if <1.20 {
    public HubChannel(UUID channelId, EntityPlayerMP player, PermissionMode permissionMode) {
    //?} else {
    /*public HubChannel(UUID channelId, ServerPlayer player, PermissionMode permissionMode) {
    *///?}
        //? if <1.20 {
        this(channelId, player.getName(), permissionMode);
        //?} else {
        /*this(channelId, player.getGameProfile().getName(), permissionMode);
        *///?}
    }

    public HubChannel(UUID channelId, String name, PermissionMode permissionMode) {
        this.channelId = channelId;
        this.name = name;
        this.permissionMode = permissionMode;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public ReferenceSet<IGrid> getGrids() {
        return grids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PermissionMode getPermissionMode() {
        return permissionMode;
    }

    public void setPermissionMode(PermissionMode permissionMode) {
        this.permissionMode = permissionMode;
    }

    public void addGrid(IGrid grid) {
        grids.add(grid);
    }

    public void removeGrid(IGrid grid) {
        grids.remove(grid);
    }
}
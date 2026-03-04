package com.circulation.circulation_networks.manager;

import com.circulation.circulation_networks.api.IGrid;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.events.TileEntityLifeCycleEvent;
import com.circulation.circulation_networks.network.hub.HubChannel;
import com.circulation.circulation_networks.tiles.nodes.TileEntityHub;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * 管理中枢频道，维护频道ID到Grid的映射关系。
 * 同频道的Grid在能量处理时会被合并处理。
 * <p>
 * Manages hub channels, maintaining the mapping from channel ID to grids.
 * Grids on the same channel will be merged during energy processing.
 */
public final class HubChannelManager {

    public static final HubChannelManager INSTANCE = new HubChannelManager();

    private final Map<UUID, HubChannel> channels = new Object2ReferenceOpenHashMap<>();
    private final Reference2ObjectMap<IHubNode, UUID> hubChannels = new Reference2ObjectOpenHashMap<>();

    /**
     * 将中枢所在Grid注册到指定频道
     */
    public void register(IHubNode hub, UUID channelId, String name, PermissionMode permissionMode) {
        if (channelId == null || name == null) return;
        var grid = hub.getGrid();
        if (grid == null) return;

        unregister(hub);

        var channel = channels.get(channelId);
        if (channel == null) {
            channel = new HubChannel(channelId, name,permissionMode);
            channels.put(channelId, channel);
        }
        channel.addGrid(grid);
        hubChannels.put(hub, channelId);
    }

    /**
     * 注销中枢的频道注册
     */
    public void unregister(IHubNode hub) {
        var oldChannelId = hubChannels.remove(hub);
        if (oldChannelId == null) return;

        var channel = channels.get(oldChannelId);
        if (channel != null) {
            var grid = hub.getGrid();
            if (grid != null) {
                channel.removeGrid(grid);
            }
            if (channel.getGrids().isEmpty()) {
                channels.remove(oldChannelId);
            }
        }
    }

    /**
     * 获取指定频道内的所有Grid
     */
    @Nullable
    public ReferenceSet<IGrid> getChannelGrids(UUID channelId) {
        var channel = channels.get(channelId);
        return channel != null ? channel.getGrids() : null;
    }

    /**
     * 当中枢TE被验证时调用（节点已被NetworkManager添加到Grid后）
     */
    public void onTileEntityValidate(TileEntityLifeCycleEvent.Validate event) {
        if (event.getWorld().isRemote) return;
        var te = event.getTileEntity();
        if (te instanceof TileEntityHub hubTE) {
            var node = hubTE.getNode();
            if (node instanceof IHubNode hub) {
                var channelId = hubTE.getChannelId();
                var channelName = hubTE.getChannelName();
                var p = hubTE.getPermissionMode();
                if (channelId != null && channelName != null) {
                    register(hub, channelId, channelName, p);
                }
            }
        }
    }

    /**
     * 当中枢TE被移除时调用（在NetworkManager移除节点之前）
     */
    public void onTileEntityInvalidate(TileEntityLifeCycleEvent.Invalidate event) {
        if (event.getWorld().isRemote) return;
        var te = event.getTileEntity();
        if (te instanceof TileEntityHub hubTE) {
            var node = hubTE.getNode();
            if (node instanceof IHubNode hub) {
                unregister(hub);
            }
        }
    }

    public void onServerStop() {
        channels.clear();
        hubChannels.clear();
    }
}

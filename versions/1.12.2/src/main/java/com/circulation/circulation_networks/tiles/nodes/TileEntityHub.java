package com.circulation.circulation_networks.tiles.nodes;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.api.IHubNodeBlockEntity;
import com.circulation.circulation_networks.api.hub.ChargingPreference;
import com.circulation.circulation_networks.api.hub.IHubPlugin;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.api.node.INode;
import com.circulation.circulation_networks.items.HubChannelPluginData;
import com.circulation.circulation_networks.network.nodes.HubNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class TileEntityHub extends BaseNodeTileEntity implements IHubNodeBlockEntity {

    private final ItemStack[] plugins = new ItemStack[IHubNode.PLUGIN_SLOTS];
    private final Map<UUID, ChargingPreference> playerPreferences = new Object2ObjectOpenHashMap<>();
    private PermissionMode permissionMode = PermissionMode.PUBLIC;
    @Nullable
    private UUID owner;
    @Nullable
    private UUID channelId;
    @Nullable
    private String channelName;

    public ItemStack[] getPlugins() { return plugins; }
    public PermissionMode getPermissionMode() { return permissionMode; }
    public void setPermissionMode(PermissionMode permissionMode) { this.permissionMode = permissionMode; }
    @Nullable public UUID getOwner() { return owner; }
    public void setOwner(@Nullable UUID owner) { this.owner = owner; }
    @Nullable public UUID getChannelId() { return channelId; }
    public void setChannelId(@Nullable UUID channelId) { this.channelId = channelId; }
    @Nullable public String getChannelName() { return channelName; }
    public void setChannelName(@Nullable String channelName) { this.channelName = channelName; }

    public TileEntityHub() {
        Arrays.fill(plugins, ItemStack.EMPTY);
    }

    @Override
    protected @NotNull INode createNode() {
        return new HubNode(this,
            CFNConfig.NODE.hub.energyScope,
            CFNConfig.NODE.hub.chargingScope,
            CFNConfig.NODE.hub.linkScope);
    }

    public void setPlugin(int slot, ItemStack stack) {
        if (slot < 0 || slot >= plugins.length) return;

        var oldStack = plugins[slot];
        if (!oldStack.isEmpty() && oldStack.getItem() instanceof IHubPlugin oldPlugin) {
            var node = getNode();
            if (node instanceof IHubNode hub) {
                oldPlugin.onRemoved(hub, slot, oldStack);
            }
        }

        plugins[slot] = stack == null ? ItemStack.EMPTY : stack;

        if (!plugins[slot].isEmpty() && plugins[slot].getItem() instanceof IHubPlugin newPlugin) {
            var node = getNode();
            if (node instanceof IHubNode hub) {
                newPlugin.onInserted(hub, slot, plugins[slot]);
            }
        }

        markDirty();
    }

    @Nonnull
    public ChargingPreference getChargingPreference(UUID playerId) {
        return playerPreferences.computeIfAbsent(playerId, k -> {
            markDirty();
            return ChargingPreference.defaultAll();
        });
    }

    public void setChargingPreference(UUID playerId, ChargingPreference preference) {
        playerPreferences.put(playerId, preference);
        markDirty();
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("permissionMode", permissionMode.getId());

        if (owner != null) {
            compound.setUniqueId("owner", owner);
        }

        var pluginList = new NBTTagList();
        for (var plugin : plugins) {
            pluginList.appendTag(plugin.writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("plugins", pluginList);

        var prefList = new NBTTagList();
        for (var entry : playerPreferences.entrySet()) {
            var prefNbt = entry.getValue().serialize();
            prefNbt.setUniqueId("player", entry.getKey());
            prefList.appendTag(prefNbt);
        }
        compound.setTag("chargingPreferences", prefList);

        return compound;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);

        permissionMode = PermissionMode.fromId(compound.getInteger("permissionMode"));

        if (compound.hasUniqueId("owner")) {
            owner = compound.getUniqueId("owner");
        }

        if (compound.hasKey("plugins", Constants.NBT.TAG_LIST)) {
            var pluginList = compound.getTagList("plugins", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < Math.min(pluginList.tagCount(), plugins.length); i++) {
                plugins[i] = new ItemStack(pluginList.getCompoundTagAt(i));
            }
        }

        playerPreferences.clear();
        if (compound.hasKey("chargingPreferences", Constants.NBT.TAG_LIST)) {
            var prefList = compound.getTagList("chargingPreferences", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < prefList.tagCount(); i++) {
                var prefNbt = prefList.getCompoundTagAt(i);
                if (prefNbt.hasUniqueId("player")) {
                    var playerId = prefNbt.getUniqueId("player");
                    playerPreferences.put(playerId, ChargingPreference.deserialize(prefNbt));
                }
            }
        }

        channelId = null;
        channelName = null;
        for (var plugin : plugins) {
            if (!plugin.isEmpty()) {
                channelId = HubChannelPluginData.getChannelId(plugin);
                channelName = HubChannelPluginData.getChannelName(plugin);
                if (HubChannelPluginData.isComplete(channelId, channelName)) {
                    break;
                }
            }
        }
    }

    @Override
    protected void onInvalidate() {
        if (getNode() != null && getNode().getGrid() != null) {
            getNode().getGrid().setHubNode(null);
        }
        super.onInvalidate();
    }
}

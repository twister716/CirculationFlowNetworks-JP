package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.INodeTileEntity;
import com.circulation.circulation_networks.api.hub.ChargingDefinition;
import com.circulation.circulation_networks.api.hub.ChargingPreference;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import com.circulation.circulation_networks.api.node.IHubNode;
import com.circulation.circulation_networks.tiles.nodes.TileEntityHub;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;


public final class HubNode extends Node implements IHubNode {

    private final double energyScope;
    private final double energyScopeSq;
    private final double chargingScope;
    private final double chargingScopeSq;

    public HubNode(NBTTagCompound tag) {
        super(tag);
        this.energyScope = tag.getDouble("energyScope");
        this.energyScopeSq = energyScope * energyScope;
        this.chargingScope = tag.getDouble("chargingScope");
        this.chargingScopeSq = chargingScope * chargingScope;
    }

    public HubNode(INodeTileEntity tileEntity, double energyScope, double chargingScope, double linkScope) {
        super(tileEntity, linkScope);
        this.energyScope = energyScope;
        this.energyScopeSq = energyScope * energyScope;
        this.chargingScope = chargingScope;
        this.chargingScopeSq = chargingScope * chargingScope;
    }

    @Override
    public double getEnergyScope() {
        return energyScope;
    }

    @Override
    public double getEnergyScopeSq() {
        return energyScopeSq;
    }

    @Override
    public double getChargingScope() {
        return chargingScope;
    }

    @Override
    public double getChargingScopeSq() {
        return chargingScopeSq;
    }

    private TileEntityHub getHubTE() {
        return (TileEntityHub) getTileEntity();
    }

    @Override
    public PermissionMode getPermissionMode() {
        return getHubTE().getPermissionMode();
    }

    @Override
    public void setPermissionMode(PermissionMode mode) {
        getHubTE().setPermissionMode(mode);
    }

    @Override
    public ItemStack[] getPlugins() {
        return getHubTE().getPlugins();
    }

    @Override
    public void setPlugin(int slot, ItemStack stack) {
        getHubTE().setPlugin(slot, stack);
    }

    @Override
    public UUID getChannelId() {
        return getHubTE().getChannelId();
    }

    @Override
    public @NotNull ChargingPreference getChargingPreference(UUID playerId) {
        return getHubTE().getChargingPreference(playerId);
    }

    @Override
    public void setChargingPreference(UUID playerId, ChargingPreference preference) {
        getHubTE().setChargingPreference(playerId, preference);
    }

    @Override
    public boolean getChargingState(UUID playerId, ChargingDefinition cd) {
        return getHubTE().getChargingPreference(playerId).getPreference(cd);
    }

    @Override
    public void setChargingState(UUID playerId, ChargingDefinition cd, boolean value) {
        getHubTE().getChargingPreference(playerId).setPreference(cd, value);
    }

    @Override
    public @Nullable UUID getOwner() {
        return getHubTE().getOwner();
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        getHubTE().setOwner(owner);
    }

    @Override
    public NBTTagCompound serialize() {
        var nbt = super.serialize();
        nbt.setDouble("energyScope", energyScope);
        nbt.setDouble("chargingScope", chargingScope);
        return nbt;
    }
}

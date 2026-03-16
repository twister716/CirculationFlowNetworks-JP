package com.circulation.circulation_networks.network.nodes;

import com.circulation.circulation_networks.api.IHubNodeBlockEntity;
import com.circulation.circulation_networks.api.INodeBlockEntity;
import com.circulation.circulation_networks.api.hub.ChargingDefinition;
import com.circulation.circulation_networks.api.hub.ChargingPreference;
import com.circulation.circulation_networks.api.hub.PermissionMode;
import com.circulation.circulation_networks.api.node.IHubNode;
//? if <1.20 {
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
*///?}
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public final class HubNode extends Node implements IHubNode {

    private final double energyScope;
    private final double energyScopeSq;
    private final double chargingScope;
    private final double chargingScopeSq;

    //? if <1.20 {
    public HubNode(NBTTagCompound tag) {
        super(tag);
        this.energyScope = tag.getDouble("energyScope");
        this.energyScopeSq = energyScope * energyScope;
        this.chargingScope = tag.getDouble("chargingScope");
        this.chargingScopeSq = chargingScope * chargingScope;
    }
    //?} else {
    /*public HubNode(CompoundTag tag) {
        super(tag);
        this.energyScope = tag.getDouble("energyScope");
        this.energyScopeSq = energyScope * energyScope;
        this.chargingScope = tag.getDouble("chargingScope");
        this.chargingScopeSq = chargingScope * chargingScope;
    }
    *///?}

    public HubNode(INodeBlockEntity blockEntity, double energyScope, double chargingScope, double linkScope) {
        super(blockEntity, linkScope);
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

    private IHubNodeBlockEntity getHubTE() {
        return (IHubNodeBlockEntity) getBlockEntity();
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
    public void setChannelId(@Nullable UUID channelId) {
        getHubTE().setChannelId(channelId);
    }

    @Override
    public @Nullable String getChannelName() {
        return getHubTE().getChannelName();
    }

    @Override
    public void setChannelName(@Nullable String channelName) {
        getHubTE().setChannelName(channelName);
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
    public boolean getChargingState(UUID playerId, ChargingDefinition chargingDefinition) {
        return getHubTE().getChargingPreference(playerId).getPreference(chargingDefinition);
    }

    @Override
    public void setChargingState(UUID playerId, ChargingDefinition chargingDefinition, boolean value) {
        getHubTE().getChargingPreference(playerId).setPreference(chargingDefinition, value);
    }

    @Override
    public @Nullable UUID getOwner() {
        return getHubTE().getOwner();
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        getHubTE().setOwner(owner);
    }

    //? if <1.20 {
    @Override
    public NBTTagCompound serialize() {
        var nbt = super.serialize();
        nbt.setDouble("energyScope", energyScope);
        nbt.setDouble("chargingScope", chargingScope);
        return nbt;
    }
    //?} else {
    /*@Override
    public CompoundTag serialize() {
        var nbt = super.serialize();
        nbt.putDouble("energyScope", energyScope);
        nbt.putDouble("chargingScope", chargingScope);
        return nbt;
    }
    *///?}
}
package com.circulation.circulation_networks.energy.handler;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.network.nodes.HubNode;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import org.jetbrains.annotations.Nullable;

public final class FEHandler implements IEnergyHandler {

    private static final Direction[] DIRECTIONS = Direction.values();

    @Nullable
    private IEnergyStorage send;
    @Nullable
    private IEnergyStorage receive;
    private EnergyType energyType;

    public FEHandler() {
    }

    private void bindStorage(@Nullable IEnergyStorage storage) {
        if (storage == null) {
            return;
        }
        if (send == null && storage.canExtract()) {
            send = storage;
        }
        if (receive == null && storage.canReceive()) {
            receive = storage;
        }
    }

    @Override
    public IEnergyHandler init(BlockEntity blockEntity, @Nullable HubNode.HubMetadata hubMetadata) {
        var level = blockEntity.getLevel();
        if (level == null) return this;
        var pos = blockEntity.getBlockPos();
        bindStorage(level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null));
        for (Direction direction : DIRECTIONS) {
            if (send != null && receive != null) break;
            bindStorage(level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction));
        }
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack, @Nullable HubNode.HubMetadata hubMetadata) {
        var ies = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (ies == null) return this;
        if (ies.canReceive()) {
            this.receive = ies;
        }
        return this;
    }

    @Override
    public void clear() {
        send = null;
        receive = null;
        energyType = null;
    }

    @Override
    public EnergyAmount extractEnergy(EnergyAmount maxExtract, @Nullable HubNode.HubMetadata hubMetadata) {
        if (send == null) return EnergyAmounts.ZERO;
        return EnergyAmount.obtain(send.extractEnergy((int) maxExtract.asLongClamped(), false));
    }

    @Override
    public EnergyAmount receiveEnergy(EnergyAmount maxReceive, @Nullable HubNode.HubMetadata hubMetadata) {
        if (receive == null) return EnergyAmounts.ZERO;
        return EnergyAmount.obtain(receive.receiveEnergy((int) maxReceive.asLongClamped(), false));
    }

    @Override
    public EnergyAmount canExtractValue(@Nullable HubNode.HubMetadata hubMetadata) {
        return send == null ? EnergyAmounts.ZERO : EnergyAmount.obtain(send.extractEnergy(Integer.MAX_VALUE, true));
    }

    @Override
    public EnergyAmount canReceiveValue(@Nullable HubNode.HubMetadata hubMetadata) {
        return receive == null ? EnergyAmounts.ZERO : EnergyAmount.obtain(receive.receiveEnergy(Integer.MAX_VALUE, true));
    }

    @Override
    public EnergyType getType(@Nullable HubNode.HubMetadata hubMetadata) {
        if (energyType == null) {
            boolean receive = this.receive != null;
            if (send != null) {
                return energyType = receive ? EnergyType.STORAGE : EnergyType.SEND;
            } else if (receive) {
                return energyType = EnergyType.RECEIVE;
            }
            return energyType = EnergyType.INVALID;
        }
        return energyType;
    }

    @Override
    public boolean canExtract(IEnergyHandler receiveHandler, @Nullable HubNode.HubMetadata hubMetadata) {
        return send != null;
    }

    @Override
    public boolean canReceive(IEnergyHandler sendHandler, @Nullable HubNode.HubMetadata hubMetadata) {
        return receive != null;
    }
}

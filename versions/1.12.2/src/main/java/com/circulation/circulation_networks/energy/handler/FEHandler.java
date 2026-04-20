package com.circulation.circulation_networks.energy.handler;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.network.nodes.HubNode;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import org.jetbrains.annotations.Nullable;

public class FEHandler implements IEnergyHandler {

    private static final int PROBE_AMOUNT = 1;

    @Nullable
    private IEnergyStorage send;
    @Nullable
    private IEnergyStorage receive;
    private EnergyType energyType;

    public FEHandler() {
    }

    private static boolean canExtractEffectively(IEnergyStorage storage) {
        return storage.extractEnergy(PROBE_AMOUNT, true) > 0;
    }

    private static boolean canReceiveEffectively(IEnergyStorage storage) {
        return storage.receiveEnergy(PROBE_AMOUNT, true) > 0;
    }

    private void bindStorage(@Nullable IEnergyStorage storage) {
        if (storage == null) {
            return;
        }
        if (send == null && canExtractEffectively(storage)) {
            send = storage;
        }
        if (receive == null && canReceiveEffectively(storage)) {
            receive = storage;
        }
    }

    @Override
    public IEnergyHandler init(TileEntity tileEntity, @Nullable HubNode.HubMetadata hubMetadata) {
        for (int i = 0; i < 6 && (this.send == null || this.receive == null); i++) {
            EnumFacing facing = EnumFacing.VALUES[i];
            bindStorage(tileEntity.getCapability(CapabilityEnergy.ENERGY, facing));
        }
        if (this.send == null || this.receive == null) {
            bindStorage(tileEntity.getCapability(CapabilityEnergy.ENERGY, null));
        }
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack, @Nullable HubNode.HubMetadata hubMetadata) {
        var ies = itemStack.getCapability(CapabilityEnergy.ENERGY, null);
        if (ies == null) return this;
        if (canReceiveEffectively(ies)) {
            this.receive = ies;
        }
        energyType = EnergyType.RECEIVE;
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

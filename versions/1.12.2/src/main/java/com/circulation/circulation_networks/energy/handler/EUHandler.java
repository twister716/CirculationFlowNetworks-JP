package com.circulation.circulation_networks.energy.handler;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("DataFlowIssue")
public final class EUHandler implements IEnergyHandler {

    private static final long max = Long.MAX_VALUE >> 2;
    private static final long maxFE = max << 2;
    @Nonnull
    private EnergyType energyType;
    @Nullable
    private IEnergySource send;

    private boolean isItem;
    private ItemStack itemStack = ItemStack.EMPTY;

    @Nullable
    private IEnergySink receive;

    public EUHandler(TileEntity tileEntity) {
        init(tileEntity);
    }

    public EUHandler(ItemStack stack) {
        init(stack);
    }

    @Override
    public IEnergyHandler init(TileEntity tileEntity) {
        isItem = false;
        IEnergyTile tile = EnergyNet.instance.getSubTile(tileEntity.getWorld(), tileEntity.getPos());
        boolean o = tile instanceof IEnergySource;
        boolean i = tile instanceof IEnergySink;
        if (o) {
            if (i) {
                energyType = EnergyType.STORAGE;
                receive = (IEnergySink) tile;
            } else energyType = EnergyType.SEND;
            send = (IEnergySource) tile;
        } else {
            energyType = EnergyType.RECEIVE;
            receive = (IEnergySink) tile;
        }
        if (!(send != null && send.getOfferedEnergy() > 0) && !(receive != null && receive.getDemandedEnergy() > 0))
            energyType = EnergyType.INVALID;
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack) {
        isItem = true;
        this.itemStack = itemStack;
        energyType = EnergyType.RECEIVE;
        return this;
    }

    @Override
    public void clear() {
        this.energyType = EnergyType.INVALID;
        this.send = null;
        this.receive = null;
        this.itemStack = ItemStack.EMPTY;
        this.isItem = false;
    }

    @Override
    public EnergyAmount receiveEnergy(EnergyAmount maxReceive) {
        if (isItem) {
            return EnergyAmount.obtain((long) ElectricItem.manager.charge(itemStack, maxReceive.asLongClamped(), Integer.MAX_VALUE, false, false));
        } else {
            EnergyAmount receivable = canReceiveValue();
            long i;
            try {
                i = Math.min(receivable.asLongClamped(), maxReceive.asLongClamped()) >> 2;
            } finally {
                receivable.recycle();
            }
            receive.injectEnergy(null, i, 0);
            return EnergyAmount.obtain(i << 2);
        }
    }

    @Override
    public EnergyAmount extractEnergy(EnergyAmount maxExtract) {
        EnergyAmount extractable = canExtractValue();
        long o;
        try {
            o = Math.min(extractable.asLongClamped(), maxExtract.asLongClamped()) >> 2;
        } finally {
            extractable.recycle();
        }
        send.drawEnergy(o);
        return EnergyAmount.obtain(o << 2);
    }

    @Override
    public EnergyAmount canExtractValue() {
        if (send == null) return EnergyAmounts.ZERO;
        if (send.getOfferedEnergy() > max) return EnergyAmount.obtain(maxFE);
        return EnergyAmount.obtain(((long) send.getOfferedEnergy()) << 2);
    }

    @Override
    public EnergyAmount canReceiveValue() {
        if (isItem) {
            return EnergyAmount.obtain((long) ElectricItem.manager.charge(itemStack, Double.MAX_VALUE, Integer.MAX_VALUE, false, true));
        } else {
            if (receive == null) return EnergyAmounts.ZERO;
            if (receive.getDemandedEnergy() > max) return EnergyAmount.obtain(maxFE);
            return EnergyAmount.obtain(((long) receive.getDemandedEnergy()) << 2);
        }
    }

    @Override
    public boolean canExtract(IEnergyHandler receiveHandler) {
        return send != null && send.getOfferedEnergy() > 0;
    }

    @Override
    public boolean canReceive(IEnergyHandler sendHandler) {
        if (isItem) {
            return ElectricItem.manager.getMaxCharge(itemStack) > 0;
        } else {
            return receive != null && receive.getDemandedEnergy() > 0;
        }
    }

    @Override
    public EnergyType getType() {
        return energyType;
    }
}

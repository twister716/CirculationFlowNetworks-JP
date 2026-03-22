package com.circulation.circulation_networks.energy.handler;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.base.IEnergyWrapper;
import mekanism.common.tier.EnergyCubeTier;
import mekanism.common.tile.TileEntityEnergyCube;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public final class MEKHandler implements IEnergyHandler {

    private static final Class<?> inductionPort;

    static {
        Class<?> temp;
        try {
            temp = Class.forName("mekanism.common.tile.multiblock.TileEntityInductionPort");
        } catch (ClassNotFoundException e) {
            try {
                temp = Class.forName("mekanism.common.tile.TileEntityInductionPort");
            } catch (ClassNotFoundException ex) {
                temp = null;
            }
        }
        inductionPort = temp;
    }

    private long maxOutput = Long.MAX_VALUE;
    @Nullable
    private IStrictEnergyStorage send;
    @Nullable
    private IStrictEnergyStorage receive;

    private boolean isItem;
    private IEnergizedItem receiveItem;
    private ItemStack stack = ItemStack.EMPTY;
    private long needEnergy;

    private EnergyType energyType = EnergyType.INVALID;
    private boolean creative;

    public MEKHandler(TileEntity tileEntity) {
        init(tileEntity);
    }

    public MEKHandler(ItemStack itemStack) {
        init(itemStack);
    }

    @Override
    public IEnergyHandler init(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityEnergyCube te) {
            creative = te.tier == EnergyCubeTier.CREATIVE;
            send = (IStrictEnergyStorage) tileEntity;
            receive = (IStrictEnergyStorage) tileEntity;
            energyType = EnergyType.STORAGE;
            maxOutput = (long) te.getMaxOutput();
        } else if (inductionPort != null && inductionPort.isInstance(tileEntity)) {
            send = (IStrictEnergyStorage) tileEntity;
            receive = (IStrictEnergyStorage) tileEntity;
            energyType = EnergyType.STORAGE;
            maxOutput = (long) ((IEnergyWrapper) tileEntity).getMaxOutput();
        } else {
            boolean a = false;
            boolean b = false;
            for (var i = 0; i < EnumFacing.VALUES.length && !(a && b); i++) {
                var f = EnumFacing.VALUES[i];
                if (receive == null && tileEntity instanceof IStrictEnergyAcceptor a1) {
                    if (a1.canReceiveEnergy(f)) {
                        receive = (IStrictEnergyStorage) tileEntity;
                        a = true;
                    }
                }
                if (send == null && tileEntity instanceof IStrictEnergyOutputter o) {
                    if (o.canOutputEnergy(f)) {
                        send = (IStrictEnergyStorage) tileEntity;
                        b = true;
                    }
                }
            }
            if (a) energyType = b ? EnergyType.STORAGE : EnergyType.RECEIVE;
            else if (b) energyType = EnergyType.SEND;

            if (tileEntity instanceof IEnergyWrapper te && te.getMaxOutput() != 0) {
                maxOutput = (long) te.getMaxOutput();
            }
        }
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack) {
        isItem = true;
        receiveItem = (IEnergizedItem) itemStack.getItem();
        double i = receiveItem.getMaxTransfer(itemStack);
        double r = (receiveItem.getMaxEnergy(itemStack) - receiveItem.getEnergy(itemStack)) / 10;
        needEnergy = (long) (i == 0 ? r : Math.min(r, i));
        stack = itemStack;
        energyType = EnergyType.RECEIVE;
        return this;
    }

    @Override
    public void clear() {
        maxOutput = Long.MAX_VALUE;
        send = null;
        receive = null;
        receiveItem = null;
        energyType = EnergyType.INVALID;
        creative = false;
        isItem = false;
        needEnergy = 0;
        stack = ItemStack.EMPTY;
    }

    @Override
    public EnergyAmount receiveEnergy(EnergyAmount maxReceive) {
        if (isItem) {
            var i = Math.min(needEnergy, maxReceive.asLongClamped());
            receiveItem.setEnergy(stack, receiveItem.getEnergy(stack) + i);
            needEnergy -= i;
            return EnergyAmount.obtain(i);
        } else {
            if (receive == null) return EnergyAmounts.ZERO;
            EnergyAmount receivable = canReceiveValue();
            long i;
            try {
                i = Math.min(receivable.asLongClamped(), maxReceive.asLongClamped());
            } finally {
                receivable.recycle();
            }
            receive.setEnergy(receive.getEnergy() + i * 2.5);
            return EnergyAmount.obtain(i);
        }
    }

    @Override
    public EnergyAmount extractEnergy(EnergyAmount maxExtract) {
        if (send == null) return EnergyAmounts.ZERO;
        EnergyAmount extractable = canExtractValue();
        long o;
        try {
            o = Math.min(extractable.asLongClamped(), maxExtract.asLongClamped());
        } finally {
            extractable.recycle();
        }
        if (!creative) send.setEnergy(send.getEnergy() - o * 2.5);
        return EnergyAmount.obtain(o);
    }

    @Override
    public EnergyAmount canExtractValue() {
        if (send == null) return EnergyAmounts.ZERO;
        if (creative) return EnergyAmounts.LONG_MAX;
        double o = send.getEnergy() * 0.4;
        return EnergyAmount.obtain(Math.min((long) o, maxOutput));
    }

    @Override
    public EnergyAmount canReceiveValue() {
        if (isItem) {
            return EnergyAmount.obtain(needEnergy);
        } else {
            if (receive == null) return EnergyAmounts.ZERO;
            double i = (receive.getMaxEnergy() - receive.getEnergy()) * 0.4;
            return EnergyAmount.obtain(Math.min((long) i, maxOutput));
        }
    }

    @Override
    public boolean canExtract(IEnergyHandler receiveHandler) {
        if (creative) return true;
        return send != null && send.getEnergy() >= 2.5;
    }

    @Override
    public boolean canReceive(IEnergyHandler sendHandler) {
        if (isItem) return needEnergy > 0;
        else return receive != null && (receive.getMaxEnergy() - receive.getEnergy()) * 0.4 >= 0;
    }

    @Override
    public EnergyType getType() {
        return energyType;
    }

}
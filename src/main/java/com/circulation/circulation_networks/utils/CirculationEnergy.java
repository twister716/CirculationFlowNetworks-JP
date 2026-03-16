package com.circulation.circulation_networks.utils;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IMachineNodeBlockEntity;
import com.circulation.circulation_networks.api.node.IMachineNode;

import java.math.BigInteger;

public class CirculationEnergy {

    private final IMachineNode node;
    private final EnergyAmount energy = new EnergyAmount(0L);

    private CirculationEnergy(IMachineNodeBlockEntity be) {
        node = be.getNode();
    }

    public static CirculationEnergy create(IMachineNodeBlockEntity be) {
        if (be.getNode() == null) return null;
        return new CirculationEnergy(be);
    }

    public EnergyAmount extractEnergy(EnergyAmount amount, boolean simulate) {
        EnergyAmount extractable = canExtractValue();
        try {
            EnergyAmount extracted = EnergyAmount.obtain(extractable);
            extracted.min(amount);
            if (!simulate) {
                energy.subtract(extracted);
            }
            return extracted;
        } finally {
            extractable.recycle();
        }
    }

    public EnergyAmount receiveEnergy(EnergyAmount amount, boolean simulate) {
        EnergyAmount receivable = canReceiveValue();
        try {
            EnergyAmount received = EnergyAmount.obtain(receivable);
            received.min(amount);
            if (!simulate) {
                energy.add(received);
            }
            return received;
        } finally {
            receivable.recycle();
        }
    }

    public EnergyAmount canExtractValue() {
        return EnergyAmount.obtain(energy);
    }

    public EnergyAmount canReceiveValue() {
        long maxEnergy = node.getMaxEnergy();
        if (energy.compareTo(maxEnergy) >= 0) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(maxEnergy).subtract(energy);
    }

    public long getEnergy() {
        return energy.asLongClamped();
    }

    public String getEnergyString() {
        return energy.toString();
    }

    public void setEnergy(long value) {
        energy.init(value);
    }

    public void setEnergy(BigInteger value) {
        energy.init(value);
    }
}

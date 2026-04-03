package com.circulation.circulation_networks.api;

import java.math.BigInteger;

public final class ConstantEnergyAmount extends EnergyAmount {

    public ConstantEnergyAmount(long value) {
        super(value);
    }

    private static UnsupportedOperationException immutableError() {
        return new UnsupportedOperationException("Constant EnergyAmount cannot be modified");
    }

    @Override
    public EnergyAmount init(long value) {
        if (state != STATE_UNINITIALIZED) throw immutableError();
        return super.init(value);
    }

    @Override
    public EnergyAmount init(String value) {
        if (state != STATE_UNINITIALIZED) throw immutableError();
        return super.init(value);
    }

    @Override
    public EnergyAmount init(BigInteger value) {
        if (state != STATE_UNINITIALIZED) throw immutableError();
        return super.init(value);
    }

    @Override
    public EnergyAmount copyFrom(EnergyAmount other) {
        if (state != STATE_UNINITIALIZED) throw immutableError();
        return super.copyFrom(other);
    }

    @Override
    public void clear() {
        throw immutableError();
    }

    @Override
    public void recycle() {
    }

    @Override
    public EnergyAmount setZero() {
        throw immutableError();
    }

    @Override
    public EnergyAmount add(long value) {
        throw immutableError();
    }

    @Override
    public EnergyAmount add(EnergyAmount other) {
        throw immutableError();
    }

    @Override
    public EnergyAmount subtract(long value) {
        throw immutableError();
    }

    @Override
    public EnergyAmount subtract(EnergyAmount other) {
        throw immutableError();
    }

    @Override
    public EnergyAmount min(EnergyAmount other) {
        throw immutableError();
    }

    @Override
    public EnergyAmount max(EnergyAmount other) {
        throw immutableError();
    }
}
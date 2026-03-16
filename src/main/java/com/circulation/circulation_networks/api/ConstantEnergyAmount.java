package com.circulation.circulation_networks.api;

import java.math.BigInteger;

final class ConstantEnergyAmount extends EnergyAmount {

    ConstantEnergyAmount(long value) {
        super();
        assignLongDirect(value);
    }

    @Override
    public EnergyAmount init(long value) {
        throw immutableError();
    }

    @Override
    public EnergyAmount init(String value) {
        throw immutableError();
    }

    @Override
    public EnergyAmount init(BigInteger value) {
        throw immutableError();
    }

    @Override
    public EnergyAmount copyFrom(EnergyAmount other) {
        throw immutableError();
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

    private static UnsupportedOperationException immutableError() {
        return new UnsupportedOperationException("Constant EnergyAmount cannot be modified");
    }
}
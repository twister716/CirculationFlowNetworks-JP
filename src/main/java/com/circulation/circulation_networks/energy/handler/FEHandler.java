package com.circulation.circulation_networks.energy.handler;

import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.network.nodes.HubNode;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import org.jetbrains.annotations.Nullable;

public final class FEHandler implements IEnergyHandler {

    private static final Direction[] DIRECTIONS = Direction.values();

    @Nullable
    private EnergyHandler send;
    @Nullable
    private EnergyHandler receive;
    private EnergyType energyType;

    public FEHandler() {
    }

    @Nullable
    private static Integer clampAmount(EnergyAmount amount) {
        return (int) amount.asLongClamped();
    }

    private static boolean canExtract(EnergyHandler handler) {
        return handler.getAmountAsLong() > 0L || simulateExtract(handler, 1) > 0;
    }

    private static boolean canReceive(EnergyHandler handler) {
        return handler.getAmountAsLong() < handler.getCapacityAsLong() || simulateInsert(handler, 1) > 0;
    }

    private static int simulateExtract(EnergyHandler handler, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            return handler.extract(amount, transaction);
        }
    }

    private static int simulateInsert(EnergyHandler handler, int amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            return handler.insert(amount, transaction);
        }
    }

    private void bindStorage(@Nullable EnergyHandler storage) {
        if (storage == null) {
            return;
        }
        if (send == null && canExtract(storage)) {
            send = storage;
        }
        if (receive == null && canReceive(storage)) {
            receive = storage;
        }
    }

    @Override
    public IEnergyHandler init(BlockEntity blockEntity, @Nullable HubNode.HubMetadata hubMetadata) {
        var level = blockEntity.getLevel();
        if (level == null) return this;
        var pos = blockEntity.getBlockPos();
        bindStorage(level.getCapability(Capabilities.Energy.BLOCK, pos, null));
        for (Direction direction : DIRECTIONS) {
            if (send != null && receive != null) break;
            bindStorage(level.getCapability(Capabilities.Energy.BLOCK, pos, direction));
        }
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack, @Nullable HubNode.HubMetadata hubMetadata) {
        if (itemStack.isEmpty()) return this;
        var ies = ItemAccess.forStack(itemStack).getCapability(Capabilities.Energy.ITEM);
        if (ies == null) return this;
        if (canReceive(ies)) {
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
        int amount = clampAmount(maxExtract);
        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = send.extract(amount, transaction);
            transaction.commit();
            return EnergyAmount.obtain(extracted);
        }
    }

    @Override
    public EnergyAmount receiveEnergy(EnergyAmount maxReceive, @Nullable HubNode.HubMetadata hubMetadata) {
        if (receive == null) return EnergyAmounts.ZERO;
        int amount = clampAmount(maxReceive);
        try (Transaction transaction = Transaction.openRoot()) {
            int inserted = receive.insert(amount, transaction);
            transaction.commit();
            return EnergyAmount.obtain(inserted);
        }
    }

    @Override
    public EnergyAmount canExtractValue(@Nullable HubNode.HubMetadata hubMetadata) {
        return send == null ? EnergyAmounts.ZERO : EnergyAmount.obtain(simulateExtract(send, Integer.MAX_VALUE));
    }

    @Override
    public EnergyAmount canReceiveValue(@Nullable HubNode.HubMetadata hubMetadata) {
        return receive == null ? EnergyAmounts.ZERO : EnergyAmount.obtain(simulateInsert(receive, Integer.MAX_VALUE));
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

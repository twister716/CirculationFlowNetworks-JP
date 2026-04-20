package com.circulation.circulation_networks.energy.handler;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.network.nodes.HubNode;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class DEHandler implements IEnergyHandler {

    private static final Direction[] DIRECTIONS = Direction.values();

    @Nullable
    private IOPStorage send;
    @Nullable
    private IOPStorage receive;
    private EnergyType energyType;

    private void bindStorage(@Nullable IOPStorage storage) {
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
        if (blockEntity instanceof TileEnergyPylon) {
            for (Direction direction : DIRECTIONS) {
                var optional = blockEntity.getCapability(CapabilityOP.OP, direction);
                if (optional.isPresent()) {
                    IOPStorage storage = optional.orElseThrow(IllegalStateException::new);
                    send = storage;
                    receive = storage;
                    return this;
                }
            }
            var optional = blockEntity.getCapability(CapabilityOP.OP, null);
            if (optional.isPresent()) {
                IOPStorage storage = optional.orElseThrow(IllegalStateException::new);
                send = storage;
                receive = storage;
            }
            return this;
        }
        for (Direction direction : DIRECTIONS) {
            if (send != null && receive != null) {
                break;
            }
            blockEntity.getCapability(CapabilityOP.OP, direction).ifPresent(this::bindStorage);
        }
        if (send == null || receive == null) {
            blockEntity.getCapability(CapabilityOP.OP, null).ifPresent(this::bindStorage);
        }
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack, @Nullable HubNode.HubMetadata hubMetadata) {
        itemStack.getCapability(CapabilityOP.OP).ifPresent(storage -> {
            if (storage.canReceive()) {
                receive = storage;
            }
        });
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
        if (send == null) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(send.extractOP(maxExtract.asLongClamped(), false));
    }

    @Override
    public EnergyAmount receiveEnergy(EnergyAmount maxReceive, @Nullable HubNode.HubMetadata hubMetadata) {
        if (receive == null) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(receive.receiveOP(maxReceive.asLongClamped(), false));
    }

    @Override
    public EnergyAmount canExtractValue(@Nullable HubNode.HubMetadata hubMetadata) {
        return send == null ? EnergyAmounts.ZERO : EnergyAmount.obtain(send.extractOP(Long.MAX_VALUE, true));
    }

    @Override
    public EnergyAmount canReceiveValue(@Nullable HubNode.HubMetadata hubMetadata) {
        return receive == null ? EnergyAmounts.ZERO : EnergyAmount.obtain(receive.receiveOP(Long.MAX_VALUE, true));
    }

    @Override
    public EnergyType getType(@Nullable HubNode.HubMetadata hubMetadata) {
        if (energyType == null) {
            boolean canReceive = receive != null;
            if (send != null) {
                return energyType = canReceive ? EnergyType.STORAGE : EnergyType.SEND;
            } else if (canReceive) {
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

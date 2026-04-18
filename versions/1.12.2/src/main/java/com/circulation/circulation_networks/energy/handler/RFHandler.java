package com.circulation.circulation_networks.energy.handler;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.circulation.circulation_networks.api.EnergyAmount;
import com.circulation.circulation_networks.api.EnergyAmounts;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.network.nodes.HubNode;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

public class RFHandler implements IEnergyHandler {

    @Nullable
    private IEnergyProvider send;
    @Nullable
    private IEnergyReceiver receive;
    @Nullable
    private IEnergyContainerItem receiveItem;
    @Nullable
    private EnumFacing sendFacing;
    @Nullable
    private EnumFacing receiveFacing;
    private ItemStack itemStack = ItemStack.EMPTY;
    private boolean isItem;
    private EnergyType energyType;

    public RFHandler() {
    }

    private static int asRfAmount(EnergyAmount amount) {
        long value = amount.asLongClamped();
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    private void bindTileSide(TileEntity tileEntity, EnumFacing facing) {
        if (!(tileEntity instanceof IEnergyConnection connection) || !connection.canConnectEnergy(facing)) {
            return;
        }
        if (send == null && tileEntity instanceof IEnergyProvider provider) {
            send = provider;
            sendFacing = facing;
        }
        if (receive == null && tileEntity instanceof IEnergyReceiver receiver) {
            receive = receiver;
            receiveFacing = facing;
        }
    }

    @Override
    public IEnergyHandler init(TileEntity tileEntity, @Nullable HubNode.HubMetadata hubMetadata) {
        isItem = false;
        for (int i = 0; i < EnumFacing.VALUES.length && (send == null || receive == null); i++) {
            bindTileSide(tileEntity, EnumFacing.VALUES[i]);
        }
        return this;
    }

    @Override
    public IEnergyHandler init(ItemStack itemStack, @Nullable HubNode.HubMetadata hubMetadata) {
        isItem = true;
        this.itemStack = itemStack;
        if (itemStack.getItem() instanceof IEnergyContainerItem containerItem) {
            receiveItem = containerItem;
            energyType = EnergyType.RECEIVE;
        }
        return this;
    }

    @Override
    public void clear() {
        send = null;
        receive = null;
        receiveItem = null;
        sendFacing = null;
        receiveFacing = null;
        itemStack = ItemStack.EMPTY;
        isItem = false;
        energyType = null;
    }

    @Override
    public EnergyAmount extractEnergy(EnergyAmount maxExtract, @Nullable HubNode.HubMetadata hubMetadata) {
        if (send == null || sendFacing == null) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(send.extractEnergy(sendFacing, asRfAmount(maxExtract), false));
    }

    @Override
    public EnergyAmount receiveEnergy(EnergyAmount maxReceive, @Nullable HubNode.HubMetadata hubMetadata) {
        if (isItem) {
            if (receiveItem == null) {
                return EnergyAmounts.ZERO;
            }
            return EnergyAmount.obtain(receiveItem.receiveEnergy(itemStack, asRfAmount(maxReceive), false));
        }
        if (receive == null || receiveFacing == null) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(receive.receiveEnergy(receiveFacing, asRfAmount(maxReceive), false));
    }

    @Override
    public EnergyAmount canExtractValue(@Nullable HubNode.HubMetadata hubMetadata) {
        if (send == null || sendFacing == null) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(send.extractEnergy(sendFacing, Integer.MAX_VALUE, true));
    }

    @Override
    public EnergyAmount canReceiveValue(@Nullable HubNode.HubMetadata hubMetadata) {
        if (isItem) {
            if (receiveItem == null) {
                return EnergyAmounts.ZERO;
            }
            return EnergyAmount.obtain(receiveItem.receiveEnergy(itemStack, Integer.MAX_VALUE, true));
        }
        if (receive == null || receiveFacing == null) {
            return EnergyAmounts.ZERO;
        }
        return EnergyAmount.obtain(receive.receiveEnergy(receiveFacing, Integer.MAX_VALUE, true));
    }

    @Override
    public EnergyType getType(@Nullable HubNode.HubMetadata hubMetadata) {
        if (energyType == null) {
            boolean hasReceive = receive != null || receiveItem != null;
            if (send != null) {
                return energyType = hasReceive ? EnergyType.STORAGE : EnergyType.SEND;
            }
            if (hasReceive) {
                return energyType = EnergyType.RECEIVE;
            }
            return energyType = EnergyType.INVALID;
        }
        return energyType;
    }

    @Override
    public boolean canExtract(IEnergyHandler receiveHandler, @Nullable HubNode.HubMetadata hubMetadata) {
        return send != null && sendFacing != null && send.extractEnergy(sendFacing, 1, true) > 0;
    }

    @Override
    public boolean canReceive(IEnergyHandler sendHandler, @Nullable HubNode.HubMetadata hubMetadata) {
        if (isItem) {
            return receiveItem != null && receiveItem.receiveEnergy(itemStack, 1, true) > 0;
        }
        return receive != null && receiveFacing != null && receive.receiveEnergy(receiveFacing, 1, true) > 0;
    }
}
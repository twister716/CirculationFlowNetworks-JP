package com.circulation.circulation_networks.api;

import com.circulation.circulation_networks.registry.RegistryEnergyHandler;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import com.circulation.circulation_networks.manager.EnergyMachineManager;
//? if <1.20 {
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
//?} else {
/*import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
*///?}

import java.util.Map;
import java.util.Queue;

public interface IEnergyHandler {

    Map<Class<? extends IEnergyHandler>, Queue<IEnergyHandler>> POOL = new Reference2ObjectOpenHashMap<>();

    //? if <1.20 {
    static IEnergyHandler release(TileEntity tileEntity) {
    //?} else {
    /*static IEnergyHandler release(BlockEntity tileEntity) {
    *///?}
        if (tileEntity instanceof IMachineNodeBlockEntity mbe) return mbe.getEnergyHandler();
        var m = RegistryEnergyHandler.getEnergyManager(tileEntity);
        if (m == null) return null;
        var q = POOL.get(m.getEnergyHandlerClass());
        if (q == null || q.isEmpty()) return m.newInstance(tileEntity);
        var t = q.poll();
        return t.init(tileEntity);
    }

    static IEnergyHandler release(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        var m = RegistryEnergyHandler.getEnergyManager(stack);
        if (m == null) return null;
        var q = POOL.get(m.getEnergyHandlerClass());
        if (q.isEmpty()) return m.newInstance(stack);
        var t = q.poll();
        return t.init(stack);
    }

    //? if <1.20 {
    IEnergyHandler init(TileEntity tileEntity);
    //?} else {
    /*IEnergyHandler init(BlockEntity blockEntity);
    *///?}

    IEnergyHandler init(ItemStack itemStack);

    void clear();

    EnergyAmount receiveEnergy(EnergyAmount maxReceive);

    EnergyAmount extractEnergy(EnergyAmount maxExtract);

    EnergyAmount canExtractValue();

    EnergyAmount canReceiveValue();

    boolean canExtract(IEnergyHandler receiveHandler);

    boolean canReceive(IEnergyHandler sendHandler);

    default void recycle() {
        this.clear();
        var queue = POOL.get(this.getClass());
        //? if <1.20 {
        if (queue != null && queue.size() < EnergyMachineManager.INSTANCE.getMachineGridMap().size()) {
            queue.add(this);
        }
        //?} else {
        /*if (queue != null) {
            queue.add(this);
        }
        *///?}
    }

    EnergyType getType();

    enum EnergyType {
        SEND,
        RECEIVE,
        STORAGE,
        INVALID
    }
}
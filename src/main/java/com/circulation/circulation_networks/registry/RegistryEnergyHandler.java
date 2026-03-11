package com.circulation.circulation_networks.registry;

import com.circulation.circulation_networks.CFNConfig;
import com.circulation.circulation_networks.api.IEnergyHandler;
import com.circulation.circulation_networks.api.IEnergyHandlerManager;
import com.circulation.circulation_networks.api.node.IMachineNode;
import com.circulation.circulation_networks.proxy.CommonProxy;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
public final class RegistryEnergyHandler {

    private static Class<?>[] blackListClass;
    private static Class<?>[] supplyBlackListClass;
    private static List<IEnergyHandlerManager> list = new ObjectArrayList<>();

    private static ReferenceSet<Class<?>> registeredBlackClasses = new ReferenceOpenHashSet<>();
    private static ReferenceSet<Class<?>> registeredSupplyBlackClasses = new ReferenceOpenHashSet<>();

    /**
     * Registers an energy handler manager. Must be called before {@link #lock()}.
     */
    public static void registerEnergyHandler(IEnergyHandlerManager manager) {
        list.add(manager);
        IEnergyHandler.POOL.put(manager.getEnergyHandlerClass(), new ArrayDeque<>());
    }

    /**
     * Registers a tile entity class to be excluded from automatic energy network integration.
     * Node-based tile entities (implementing {@link IMachineNode}) are automatically blacklisted
     * and do not need to be registered here.
     * Must be called before {@link #lock()}.
     *
     * @param clazz the tile entity class to blacklist from energy handling
     */
    public static void registerBlackClass(Class<?> clazz) {
        registeredBlackClasses.add(clazz);
    }

    /**
     * Registers a tile entity class to be excluded from energy supply operations.
     * Must be called before {@link #lock()}.
     *
     * @param clazz the tile entity class to blacklist from energy supply
     */
    public static void registerSupplyBlackClass(Class<?> clazz) {
        registeredSupplyBlackClasses.add(clazz);
    }

    public static boolean isBlack(TileEntity tileEntity) {
        if (tileEntity.getCapability(CommonProxy.nodeCapability, null) instanceof IMachineNode) return true;
        if (blackListClass == null) return false;
        for (Class<?> listClass : blackListClass) {
            if (listClass.isInstance(tileEntity)) return true;
        }
        return false;
    }

    public static boolean isSupplyBlack(TileEntity tileEntity) {
        if (supplyBlackListClass == null) return false;
        for (Class<?> listClass : supplyBlackListClass) {
            if (listClass.isInstance(tileEntity)) return true;
        }
        return false;
    }

    public static boolean isEnergyItemStack(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (IEnergyHandlerManager manager : list) {
            if (manager.isAvailable(stack)) return true;
        }
        return false;
    }

    public static boolean isEnergyTileEntity(TileEntity tile) {
        for (IEnergyHandlerManager manager : list) {
            if (manager.isAvailable(tile)) return true;
        }
        return false;
    }

    public static IEnergyHandlerManager getEnergyManager(TileEntity tile) {
        for (IEnergyHandlerManager manager : list) {
            if (manager.isAvailable(tile)) return manager;
        }
        return null;
    }

    public static IEnergyHandlerManager getEnergyManager(ItemStack stack) {
        for (IEnergyHandlerManager manager : list) {
            if (manager.isAvailable(stack)) return manager;
        }
        return null;
    }

    public static void lock() {
        list.sort(Comparator.reverseOrder());
        list = ImmutableList.copyOf(list);

        final List<String> blackPrefixes = new ObjectArrayList<>();
        final List<String> supplyPrefixes = new ObjectArrayList<>();
        final ReferenceSet<Class<?>> blackSet = registeredBlackClasses;
        final ReferenceSet<Class<?>> supplySet = registeredSupplyBlackClasses;

        collectExactClasses(CFNConfig.classNames, blackSet, blackPrefixes);
        collectExactClasses(CFNConfig.supplyClassNames, supplySet, supplyPrefixes);

        if (!blackPrefixes.isEmpty() || !supplyPrefixes.isEmpty()) {
            for (var aClass : TileEntity.REGISTRY) {
                var className = aClass.getName();
                if (!blackPrefixes.isEmpty() && !blackSet.contains(aClass)) {
                    for (String prefix : blackPrefixes) {
                        if (className.startsWith(prefix)) {
                            blackSet.add(aClass);
                            break;
                        }
                    }
                }
                if (!supplyPrefixes.isEmpty() && !supplySet.contains(aClass)) {
                    for (String prefix : supplyPrefixes) {
                        if (className.startsWith(prefix)) {
                            supplySet.add(aClass);
                            break;
                        }
                    }
                }
            }
        }

        blackListClass = blackSet.isEmpty() ? null : blackSet.toArray(new Class[0]);
        supplyBlackListClass = supplySet.isEmpty() ? null : supplySet.toArray(new Class[0]);

        registeredBlackClasses.clear();
        registeredSupplyBlackClasses.clear();

        registeredBlackClasses = null;
        registeredSupplyBlackClasses = null;
    }

    private static void collectExactClasses(String[] names, ReferenceSet<Class<?>> classSet, List<String> prefixes) {
        if (names == null) return;
        for (String className : names) {
            if (className == null || className.trim().isEmpty()) continue;
            className = className.trim();
            try {
                classSet.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                prefixes.add(className);
            }
        }
    }

}
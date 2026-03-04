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
    private static List<IEnergyHandlerManager> list = new ObjectArrayList<>();

    /**
     * 只允许在postinit阶段前进行注册
     */
    public static void registerEnergyHandler(IEnergyHandlerManager manager) {
        list.add(manager);
        IEnergyHandler.POOL.put(manager.getEnergyHandlerClass(), new ArrayDeque<>());
    }

    public static boolean isBlack(TileEntity tileEntity) {
        if (tileEntity.getCapability(CommonProxy.nodeCapability, null) instanceof IMachineNode) return true;
        if (blackListClass == null) return false;
        for (Class<?> listClass : blackListClass) {
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

        if (CFNConfig.classNames != null && CFNConfig.classNames.length > 0) {
            final List<String> blackNameList = new ObjectArrayList<>();
            final ReferenceSet<Class<?>> blackListClass = new ReferenceOpenHashSet<>();

            for (String className : CFNConfig.classNames) {
                if (className == null || className.trim().isEmpty()) continue;
                className = className.trim();

                try {
                    Class<?> cls = Class.forName(className);
                    blackListClass.add(cls);
                } catch (ClassNotFoundException e) {
                    blackNameList.add(className);
                }
            }

            for (var aClass : TileEntity.REGISTRY) {
                if (blackListClass.contains(aClass)) continue;
                var className = aClass.getName();
                for (String s : blackNameList) {
                    if (className.startsWith(s)) {
                        blackListClass.add(aClass);
                        break;
                    }
                }
            }

            RegistryEnergyHandler.blackListClass = blackListClass.toArray(new Class[0]);
        }
    }

}
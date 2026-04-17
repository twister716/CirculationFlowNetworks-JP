package com.circulation.circulation_networks.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Consumer;

public final class AccessoryInventoryCompat {

    private static final boolean ACCESSORY_INTEGRATION_LOADED = ModList.get().isLoaded("curios");

    private AccessoryInventoryCompat() {
    }

    public static boolean isAccessoryIntegrationLoaded() {
        return ACCESSORY_INTEGRATION_LOADED;
    }

    public static void collectAccessoryItems(Player player, Consumer<ItemStack> consumer) {
        CuriosApi.getCuriosInventory(player).ifPresent(curiosHandler -> {
            for (var curiosStackHandler : curiosHandler.getCurios().values()) {
                var equippedCurios = curiosStackHandler.getStacks();
                for (int i = 0; i < equippedCurios.getSlots(); i++) {
                    consumer.accept(equippedCurios.getStackInSlot(i));
                }
            }
        });
    }
}

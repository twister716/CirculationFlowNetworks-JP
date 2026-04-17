package com.circulation.circulation_networks.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PlayerInventoryCompat {

    private PlayerInventoryCompat() {
    }

    public static List<ItemStack> getMainInventory(Player player) {
        return player.getInventory().getNonEquipmentItems();
    }

    public static List<ItemStack> getArmorInventory(Player player) {
        List<ItemStack> armor = new ObjectArrayList<>(4);
        armor.add(player.getItemBySlot(EquipmentSlot.FEET));
        armor.add(player.getItemBySlot(EquipmentSlot.LEGS));
        armor.add(player.getItemBySlot(EquipmentSlot.CHEST));
        armor.add(player.getItemBySlot(EquipmentSlot.HEAD));
        return armor;
    }
}

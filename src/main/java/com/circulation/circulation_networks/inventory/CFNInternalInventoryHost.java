package com.circulation.circulation_networks.inventory;


import net.minecraft.world.item.ItemStack;

public interface CFNInternalInventoryHost {

    void onChangeInventory(CFNInternalInventory inventory,
                           int slot,
                           CFNInventoryChangeOperation operation,
                           ItemStack oldStack,
                           ItemStack newStack);
}

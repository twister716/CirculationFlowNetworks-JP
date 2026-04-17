package com.circulation.circulation_networks.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ContainerMenuCompat {

    private ContainerMenuCompat() {
    }

    public static int getSlotCount(CFNBaseContainer container) {
        return container.slots.size();
    }

    public static void addSlot(CFNBaseContainer container, Slot slot) {
        container.cfnAddSlot(slot);
    }

    public static Slot getSlot(CFNBaseContainer container, int index) {
        return container.slots.get(index);
    }

    public static boolean hasItem(Slot slot) {
        return slot.hasItem();
    }

    public static ItemStack getItem(Slot slot) {
        return slot.getItem();
    }

    public static void onQuickTransfer(Slot slot, ItemStack stack, ItemStack copiedStack) {
        slot.onQuickCraft(stack, copiedStack);
    }

    public static void updateSlotAfterTransfer(Slot slot, ItemStack stack) {
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
    }

    public static boolean moveItemStack(CFNBaseContainer container, ItemStack stack, int start, int end, boolean reverseDirection) {
        return container.cfnMoveItemStack(stack, start, end, reverseDirection);
    }

    public static ItemStack getCarriedStack(
        CFNBaseContainer container,
        Player player
    ) {
        return container.getCarried();
    }
}

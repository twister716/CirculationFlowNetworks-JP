package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.utils.GuiSync;
import com.circulation.circulation_networks.utils.SyncData;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

public abstract class CFNBaseContainer extends Container {

    protected final TileEntity te;
    protected final EntityPlayer player;
    private final Int2ObjectMap<SyncData> syncData = new Int2ObjectOpenHashMap<>();
    private final List<LayoutEntry> layouts = new ObjectArrayList<>();

    {
        for (Field f : this.getClass().getFields()) {
            if (f.isAnnotationPresent(GuiSync.class)) {
                GuiSync annotation = f.getAnnotation(GuiSync.class);
                if (this.syncData.containsKey(annotation.value())) {
                    CirculationFlowNetworks.LOGGER.warn("Channel already in use: {} for {}", annotation.value(), f.getName());
                } else {
                    this.syncData.put(annotation.value(), new SyncData(this, f, annotation));
                }
            }
        }
    }

    public CFNBaseContainer(EntityPlayer player, TileEntity te) {
        this.te = te;
        this.player = player;
    }

    protected void registerLayout(ComponentSlotLayout layout) {
        int start = inventorySlots.size();
        layout.registerInto(this::addSlotToContainer);
        layouts.add(new LayoutEntry(layout, start, inventorySlots.size(), false));
    }

    protected void registerPlayerLayout(ComponentSlotLayout layout) {
        int start = inventorySlots.size();
        layout.registerInto(this::addSlotToContainer);
        layouts.add(new LayoutEntry(layout, start, inventorySlots.size(), true));
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();

        if (slot instanceof FilterComponentSlot) return result;

        LayoutEntry source = null;
        for (LayoutEntry e : layouts) {
            if (index >= e.start && index < e.end) {
                source = e;
                break;
            }
        }

        boolean merged = false;
        if (slot instanceof OutputComponentSlot) {
            for (LayoutEntry e : layouts) {
                if (e.isPlayerInventory) {
                    merged = mergeItemStack(stack, e.start, e.end, true);
                    if (merged) break;
                }
            }
            if (merged) slot.onSlotChange(stack, result);
        } else if (source != null && source.isPlayerInventory) {
            for (LayoutEntry e : layouts) {
                if (!e.isPlayerInventory) {
                    merged = mergeItemStack(stack, e.start, e.end, false);
                    if (merged) break;
                }
            }
        } else {
            for (LayoutEntry e : layouts) {
                if (e.isPlayerInventory) {
                    merged = mergeItemStack(stack, e.start, e.end, false);
                    if (merged) break;
                }
            }
        }

        if (!merged) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return result;
    }

    @Override
    public @NotNull ItemStack slotClick(int slotId, int dragType, @NotNull ClickType clickTypeIn, @NotNull EntityPlayer player) {
        if (slotId >= 0 && slotId < inventorySlots.size()) {
            Slot slot = inventorySlots.get(slotId);
            if (slot instanceof FilterComponentSlot) {
                if (clickTypeIn == ClickType.PICKUP) {
                    ((FilterComponentSlot) slot).ghostClickWith(player.inventory.getItemStack(), dragType);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return true;
    }

    public void detectAndSendChanges() {
        if (isServer()) {
            for (IContainerListener listener : this.listeners) {
                for (SyncData sd : this.syncData.values()) {
                    sd.tick(listener);
                }
            }
        }

        super.detectAndSendChanges();
    }

    protected final boolean isServer() {
        return !te.getWorld().isRemote;
    }

    public final void updateFullProgressBar(int idx, long value) {
        if (this.syncData.containsKey(idx)) {
            this.syncData.get(idx).update(value);
        } else {
            this.updateProgressBar(idx, (int) value);
        }
    }

    public final void stringSync(int idx, String value) {
        if (this.syncData.containsKey(idx)) {
            this.syncData.get(idx).update(value);
        }

    }

    public final void updateProgressBar(int idx, int value) {
        if (this.syncData.containsKey(idx)) {
            this.syncData.get(idx).update((long) value);
        }
    }

    public void onUpdate(final String field, final Object oldValue, final Object newValue) {

    }

    private static final class LayoutEntry {
        final ComponentSlotLayout layout;
        final int start;
        final int end;
        final boolean isPlayerInventory;

        LayoutEntry(ComponentSlotLayout layout, int start, int end, boolean isPlayerInventory) {
            this.layout = layout;
            this.start = start;
            this.end = end;
            this.isPlayerInventory = isPlayerInventory;
        }
    }
}

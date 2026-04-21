package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.utils.GuiSyncManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class CFNBaseContainer extends AbstractContainerMenu {

    protected final Player player;
    private final GuiSyncManager guiSyncManager = new GuiSyncManager();
    private final ObjectList<LayoutEntry> layouts = new ObjectArrayList<>();

    {
        guiSyncManager.scan(this, this::onUpdate);
    }

    public CFNBaseContainer(MenuType<?> menuType, int containerId, Player player) {
        super(menuType, containerId);
        this.player = player;
    }

    public Inventory getPlayerInventory() {
        return player.getInventory();
    }

    protected void registerLayout(ComponentSlotLayout layout) {
        registerLayoutInternal(layout, false);
    }

    public ComponentSlotLayout registerPlayerLayout(ComponentSlotLayout layout) {
        registerLayoutInternal(layout, true);
        return layout;
    }

    private void registerLayoutInternal(ComponentSlotLayout layout, boolean isPlayerInventory) {
        int start = ContainerMenuCompat.getSlotCount(this);
        layout.registerInto(slot -> ContainerMenuCompat.addSlot(this, slot));
        int end = ContainerMenuCompat.getSlotCount(this);
        layouts.add(new LayoutEntry(layout, start, end, isPlayerInventory));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot slot = ContainerMenuCompat.getSlot(this, index);
        if (slot == null || !ContainerMenuCompat.hasItem(slot)) return ItemStack.EMPTY;

        ItemStack stack = ContainerMenuCompat.getItem(slot);
        ItemStack result = stack.copy();

        if (slot instanceof FilterComponentSlot) return result;
        if (slot instanceof HubPluginSlot pluginSlot && !pluginSlot.canModify()) return result;

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
                    merged = mergeStack(stack, e.start, e.end, true);
                    if (merged) break;
                }
            }
            if (merged) ContainerMenuCompat.onQuickTransfer(slot, stack, result);
        } else if (source != null && source.isPlayerInventory) {
            for (LayoutEntry e : layouts) {
                if (!e.isPlayerInventory) {
                    merged = mergeStack(stack, e.start, e.end, false);
                    if (merged) break;
                }
            }
        } else {
            for (LayoutEntry e : layouts) {
                if (e.isPlayerInventory) {
                    merged = mergeStack(stack, e.start, e.end, false);
                    if (merged) break;
                }
            }
        }

        if (!merged) return ItemStack.EMPTY;
        ContainerMenuCompat.updateSlotAfterTransfer(slot, stack);
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return result;
    }

    private boolean mergeStack(ItemStack stack, int start, int end, boolean reverseDirection) {
        return ContainerMenuCompat.moveItemStack(this, stack, start, end, reverseDirection);
    }

    void cfnAddSlot(Slot slot) {
        addSlot(slot);
    }

    boolean cfnMoveItemStack(ItemStack stack, int start, int end, boolean reverseDirection) {
        return moveItemStackTo(stack, start, end, reverseDirection);
    }

    private boolean handleFilterSlotClick(int slotId, int button, ContainerInput clickType, ItemStack carried) {
        if (slotId < 0 || slotId >= ContainerMenuCompat.getSlotCount(this)) return false;
        Slot slot = ContainerMenuCompat.getSlot(this, slotId);
        if (!(slot instanceof FilterComponentSlot)) return false;
        if (clickType == ContainerInput.PICKUP) {
            ((FilterComponentSlot) slot).ghostClickWith(carried, button);
        }
        return true;
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ContainerInput clickType, @NotNull Player player) {
        if (handleFilterSlotClick(slotId, button, clickType, getCarried())) return;
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Override
    public void broadcastChanges() {
        if (isServer()) {
            ContainerSyncCompat.detectAndSendChanges(this, guiSyncManager);
        }
        super.broadcastChanges();
    }

    protected final boolean isServer() {
        return !player.level().isClientSide();
    }

    public final void updateFullProgressBar(int idx, long value) {
        if (guiSyncManager.hasChannel(idx)) {
            guiSyncManager.updateField(idx, value);
        } else {
            this.cfnUpdateProgressBar(idx, (int) value);
        }
    }

    public final void stringSync(int idx, String value) {
        guiSyncManager.updateField(idx, value);
    }

    public final void bytesSync(int idx, byte[] value) {
        guiSyncManager.updateField(idx, value);
    }

    public final void cfnUpdateProgressBar(int idx, int value) {
        guiSyncManager.updateField(idx, (long) value);
    }

    public void init() {
        guiSyncManager.init();
    }

    public void onUpdate(final String field, final Object oldValue, final Object newValue) {

    }


    private record LayoutEntry(ComponentSlotLayout layout, int start, int end, boolean isPlayerInventory) {
    }
}

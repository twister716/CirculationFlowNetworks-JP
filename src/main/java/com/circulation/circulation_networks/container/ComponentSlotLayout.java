package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.gui.component.base.Component;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Shared descriptor that binds a set of inventory slots to a GUI component.
 * <p>
 * <b>Container side:</b> call {@link #registerInto} inside the container constructor
 * (via {@code CFNBaseContainer.registerLayout}) to create the {@link ComponentSlot}s
 * and add them to the container's slot list.
 * <p>
 * <b>GUI side:</b> call {@link Component#bindLayout}
 * with the same instance to let the component drive slot positions each frame.
 * <p>
 * Thread safety: {@link #registerInto} is expected to run before the GUI is shown;
 * {@link #syncPositions} runs on the render thread only — no concurrent access.
 */
@SuppressWarnings("unused")
public class ComponentSlotLayout {

    private final List<SlotSpec> specs = new ObjectArrayList<>();
    private final List<ComponentSlot> prebuilt = new ObjectArrayList<>();
    private final List<ComponentSlot> slots = new ObjectArrayList<>();
    private boolean registered = false;

    /**
     * Creates a layout for the standard 36-slot player inventory (3×9 main + 9 hotbar),
     * with absolute slot positions pre-computed for the given component origin ({@code x}, {@code y}).
     * <p>
     * This prevents double-addition of coordinates during {@link #syncPositions},
     * since slot positions are already final and only visibility updates during sync.
     *
     * @param inventoryPlayer the player inventory
     */
    public static ComponentSlotLayout playerInventory(InventoryPlayer inventoryPlayer) {
        ComponentSlotLayout layout = new ComponentSlotLayout();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                layout.addSlot(inventoryPlayer, j + i * 9 + 9, 2 + j * 18, 2 + i * 18);
            }
        }
        for (int k = 0; k < 9; k++) {
            layout.addSlot(inventoryPlayer, k, 2 + k * 18, 60);
        }
        return layout;
    }

    /**
     * Declares a slot at the given component-relative position. Must be called
     * before {@link #registerInto}; throws if called after.
     *
     * @return {@code this} for fluent chaining
     */
    public ComponentSlotLayout addSlot(IInventory inventory, int index, int relX, int relY) {
        if (registered) {
            throw new IllegalStateException("Cannot add slot specs after registerInto() has been called");
        }
        specs.add(new SlotSpec(inventory, index, relX, relY));
        return this;
    }

    public ComponentSlotLayout addPrebuilt(ComponentSlot slot) {
        if (registered) {
            throw new IllegalStateException("Cannot add slots after registerInto() has been called");
        }
        prebuilt.add(slot);
        return this;
    }

    public ComponentSlotLayout addOutput(IInventory inventory, int index, int relX, int relY) {
        return addPrebuilt(new OutputComponentSlot(inventory, index, relX, relY));
    }

    public ComponentSlotLayout addFilter(IInventory inventory, int index, int relX, int relY, int maxCount) {
        return addPrebuilt(new FilterComponentSlot(inventory, index, relX, relY, maxCount));
    }

    /**
     * Creates {@link ComponentSlot} instances and passes each to {@code adder}
     * (typically {@code container::addSlotToContainer}).
     * Subsequent calls are no-ops (idempotent guard).
     */
    public void registerInto(Consumer<ComponentSlot> adder) {
        if (registered) return;
        registered = true;
        for (SlotSpec spec : specs) {
            ComponentSlot slot = new ComponentSlot(spec.inventory, spec.index, spec.relX, spec.relY);
            adder.accept(slot);
            slots.add(slot);
        }
        for (ComponentSlot slot : prebuilt) {
            adder.accept(slot);
            slots.add(slot);
        }
    }

    /**
     * Updates the screen-absolute position and visibility of every slot.
     * <p>
     * Always writes the real position so it is immediately correct when the slot
     * becomes visible again. Visibility is controlled via
     * {@link ComponentSlot#setVisible}: when {@code false}, {@code GuiContainer}
     * skips both rendering and mouse-click detection for the slot entirely
     * (no off-screen coordinate tricks needed).
     * Called automatically each frame by the bound {@code Component}.
     */
    public void syncPositions(int absX, int absY, boolean visible) {
        for (ComponentSlot slot : slots) {
            slot.xPos = absX + slot.getRelX();
            slot.yPos = absY + slot.getRelY();
            slot.setVisible(visible);
        }
    }

    public List<ComponentSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    private static final class SlotSpec {
        final IInventory inventory;
        final int index, relX, relY;

        SlotSpec(IInventory inventory, int index, int relX, int relY) {
            this.inventory = inventory;
            this.index = index;
            this.relX = relX;
            this.relY = relY;
        }
    }
}

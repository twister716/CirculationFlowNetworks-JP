package com.circulation.circulation_networks.gui.component.base;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * A {@link Component} variant that can be dragged by the player with the mouse.
 * <p>
 * <b>Lifecycle (managed cooperatively with
 * {@link CFNBaseGui}):</b>
 * <ol>
 *   <li>{@link #onMouseClicked} calls {@link #startDrag} → sets {@code dragging = true},
 *       computes drag offset, fires {@link #onDragStart}.</li>
 *   <li>The GUI detects {@link #isDragging()} after dispatch and stores this component
 *       as the active drag target ({@code dragTarget}).</li>
 *   <li>Every frame the GUI calls {@link #handleDrag} with the current mouse position;
 *       the component moves accordingly and fires {@link #onDrag}.</li>
 *   <li>On mouse release the GUI calls {@link #stopDrag}, which does a final position
 *       update, clears the dragging flag, and fires {@link #onDragEnd}.</li>
 * </ol>
 * <p>
 * <b>Coordinate note:</b> {@code dragBounds} constrains the component's top-left corner
 * using the same coordinate space as {@link #getX()}/{@link #getY()} (i.e., relative to
 * the parent's absolute position, or screen-absolute if there is no parent).
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public abstract class DraggableComponent extends Component {

    /**
     * -- SETTER --
     * Enables or disables dragging for this component at runtime.
     */
    @Getter
    @Setter
    private boolean draggable = true;
    /**
     * -- GETTER --
     * while a drag operation is in progress.
     */
    @Getter
    private boolean dragging = false;

    /**
     * Optional drag-position constraint: {@code [minX, minY, maxX, maxY]}.
     * Values are in the same space as {@link #getX()}/{@link #getY()}.
     * {@code null} means no constraint.
     */
    @Nullable
    @Setter
    @Getter
    private int[] dragBounds = null;

    /**
     * Cursor offset within the component when the drag started.
     */
    private int dragOffsetX;
    private int dragOffsetY;

    protected DraggableComponent(int x, int y, int width, int height, CFNBaseGui gui) {
        super(x, y, width, height, gui);
    }

    // -------------------------------------------------------------------------
    // Drag lifecycle
    // -------------------------------------------------------------------------

    /**
     * Begins a drag operation: records the cursor offset, sets the dragging flag,
     * and fires {@link #onDragStart}.
     * <p>
     * Called automatically from {@link #onMouseClicked} when
     * {@link #isDraggable()} is {@code true} and the left button is pressed.
     */
    public void startDrag(int mouseX, int mouseY) {
        dragging = true;
        dragOffsetX = mouseX - getAbsoluteX();
        dragOffsetY = mouseY - getAbsoluteY();
        onDragStart(mouseX, mouseY);
    }

    /**
     * Updates the component's position to follow the cursor.
     * If {@link #dragBounds} is set the new position is clamped to those bounds.
     * Fires {@link #onDrag} with the net displacement applied this frame.
     * <p>
     * Called by the GUI every frame while this component is the active drag target.
     */
    public void handleDrag(int mouseX, int mouseY) {
        if (!dragging) return;

        int parentAbsX = (getParent() != null) ? getParent().getAbsoluteX() : 0;
        int parentAbsY = (getParent() != null) ? getParent().getAbsoluteY() : 0;

        // Convert screen position to parent-relative position
        int newX = mouseX - dragOffsetX - parentAbsX;
        int newY = mouseY - dragOffsetY - parentAbsY;

        if (dragBounds != null) {
            newX = Math.max(dragBounds[0], Math.min(dragBounds[2], newX));
            newY = Math.max(dragBounds[1], Math.min(dragBounds[3], newY));
        }

        int deltaX = newX - getX();
        int deltaY = newY - getY();
        setX(newX);
        setY(newY);
        update = true;

        onDrag(deltaX, deltaY);
    }

    /**
     * Finalizes a drag: performs one last {@link #handleDrag}, clears the dragging flag,
     * and fires {@link #onDragEnd}.
     * <p>
     * Called by the GUI on mouse release.
     */
    public void stopDrag(int mouseX, int mouseY) {
        if (!dragging) return;
        handleDrag(mouseX, mouseY);
        dragging = false;
        onDragEnd(mouseX, mouseY);
    }

    // -------------------------------------------------------------------------
    // Mouse event — starts drag on left-click
    // -------------------------------------------------------------------------

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int button) {
        if (draggable && button == 0) {
            startDrag(mouseX, mouseY);
            // Return true so the GUI detects isDragging() and stores this as dragTarget
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Drag event hooks — override in sub-classes
    // -------------------------------------------------------------------------

    /**
     * Fired when the drag starts (cursor pressed inside this component).
     *
     * @param mouseX screen-absolute cursor X at the moment of press
     * @param mouseY screen-absolute cursor Y at the moment of press
     */
    protected void onDragStart(int mouseX, int mouseY) {
    }

    /**
     * Fired every frame while the component is being dragged.
     *
     * @param deltaX horizontal displacement applied this frame (parent-relative, after clamping)
     * @param deltaY vertical displacement applied this frame (parent-relative, after clamping)
     */
    protected void onDrag(int deltaX, int deltaY) {
    }

    /**
     * Fired when the drag ends (mouse button released).
     *
     * @param mouseX screen-absolute cursor X at the moment of release
     * @param mouseY screen-absolute cursor Y at the moment of release
     */
    protected void onDragEnd(int mouseX, int mouseY) {
    }

}

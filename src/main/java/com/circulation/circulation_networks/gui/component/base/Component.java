package com.circulation.circulation_networks.gui.component.base;

import com.circulation.circulation_networks.container.ComponentSlotLayout;
import com.circulation.circulation_networks.gui.CFNBaseGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for all CFN GUI components (buttons, labels, progress bars, etc.).
 * <p>
 * <b>Coordinate system:</b> Root-level components (added directly to a
 * {@link CFNBaseGui}) use screen-absolute
 * coordinates. Child component {@code x}/{@code y} are relative to the parent's absolute
 * position, accumulated recursively via {@link #getAbsoluteX()} / {@link #getAbsoluteY()}.
 * <p>
 * <b>Rendering:</b> {@link #renderComponent} renders this component and then all children
 * in ascending z-index order (lowest z-index rendered first; highest appears on top).
 * <p>
 * <b>Event propagation:</b> Pointer events ({@code mouseClicked}, {@code mouseReleased},
 * {@code mouseScrolled}) check {@link #contains} before descending into children, then into
 * self. The first handler that returns {@code true} consumes the event and stops propagation.
 * Key events are broadcast to all enabled components without a position check; the first
 * consumer stops propagation.
 * <p>
 * <b>Tooltips:</b> Components do NOT draw their own tooltips. Instead,
 * {@link #collectTooltip} walks the tree and returns the data; the GUI renders it.
 * <p>
 * Extend {@link Gui} so all subclasses have convenient access to {@code drawRect},
 * {@code drawTexturedModalRect}, etc.
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public abstract class Component extends Gui {

    /**
     * -- GETTER --
     * Returns the live, z-index-sorted child list. Do not modify externally.
     */
    @Getter
    private final List<Component> children = new ObjectArrayList<>();
    private final List<ComponentSlotLayout> boundLayouts = new ObjectArrayList<>();
    @Nonnull
    private final CFNBaseGui gui;
    @Getter
    protected int width;
    @Getter
    protected int height;
    @Getter
    protected boolean visible = true;
    @Setter
    @Getter
    protected boolean enabled = true;
    @Getter
    protected int zIndex = 0;
    protected boolean update;
    @Getter
    private int x;
    @Getter
    private int y;
    @Nullable
    private Component parent;
    /**
     * -- GETTER --
     * when the cursor was inside this component's bounds on the last frame.
     */
    @Getter
    private boolean hovered = false;

    // ── Sprite layer state ─────────────────────────────────────────────────────
    /**
     * Ordered array of atlas sprite base-names (no extension) rendered
     * bottom-to-top at this component's absolute bounds before {@link #render}
     * is called. Populated via {@link #setSpriteLayers}.
     */
    private String[] spriteLayers = new String[0];

    protected Component(int x, int y, int width, int height, @Nonnull CFNBaseGui gui) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gui = gui;
        update = true;
    }

    /**
     * Renders a single atlas sprite at an arbitrary screen position and size.
     * Use this for partial or offset renders (e.g., progress bars, icon overlays).
     * For standard full-component layers, prefer {@link #setSpriteLayers} or
     * {@link #getActiveLayers} so that the base class handles binding.
     * <p>The atlas texture is bound before drawing.
     *
     * @param spriteName base filename without extension, e.g. {@code "inventory"}
     * @param screenX    left edge in screen pixels
     * @param screenY    top edge in screen pixels
     * @param renderW    rendered width  in screen pixels
     * @param renderH    rendered height in screen pixels
     */
    protected static void renderAtlasSprite(String spriteName,
                                            int screenX, int screenY,
                                            int renderW, int renderH) {
        ComponentAtlas atlas = ComponentAtlas.INSTANCE;
        AtlasRegion region = atlas.getRegion(spriteName);
        if (region == null) return;

        atlas.bind();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(screenX, screenY + renderH, 0).tex(region.u0(), region.v1()).endVertex();
        buf.pos(screenX + renderW, screenY + renderH, 0).tex(region.u1(), region.v1()).endVertex();
        buf.pos(screenX + renderW, screenY, 0).tex(region.u1(), region.v0()).endVertex();
        buf.pos(screenX, screenY, 0).tex(region.u0(), region.v0()).endVertex();
        tess.draw();
    }

    // -------------------------------------------------------------------------
    // Child management
    // -------------------------------------------------------------------------

    public void setX(int x) {
        this.x = x;
        update = true;
    }

    public void setY(int y) {
        this.y = y;
        update = true;
    }

    // -------------------------------------------------------------------------
    // Coordinate helpers
    // -------------------------------------------------------------------------

    /**
     * Attaches {@code child} as a child of this component.
     * The child's {@code x}/{@code y} are interpreted relative to this component's
     * absolute position. Children are kept sorted by z-index (ascending).
     */
    public void addChild(Component child) {
        child.parent = this;
        children.add(child);
        children.sort(Comparator.comparingInt(c -> c.zIndex));
    }

    /**
     * Detaches {@code child} from this component.
     */
    public void removeChild(Component child) {
        if (children.remove(child)) {
            child.parent = null;
        }
    }

    /**
     * Screen-absolute X, accumulated by walking up the parent chain.
     */
    public int getAbsoluteX() {
        return parent != null ? parent.getAbsoluteX() + x : x;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    /**
     * Screen-absolute Y, accumulated by walking up the parent chain.
     */
    public int getAbsoluteY() {
        return parent != null ? parent.getAbsoluteY() + y : y;
    }

    /**
     * Returns {@code true} when the given screen coordinate falls inside this
     * component's bounding rectangle and the component is {@link #visible}.
     */
    public boolean contains(int mouseX, int mouseY) {
        if (!isVisible()) return false;
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        return mouseX >= ax && mouseX < ax + width
            && mouseY >= ay && mouseY < ay + height;
    }

    // -------------------------------------------------------------------------
    // Atlas sprite layer API
    // -------------------------------------------------------------------------

    /**
     * Called by the GUI each frame. Updates hover state, renders all declared
     * sprite layers, delegates to {@link #render}, then recursively renders all
     * children in ascending z-index order.
     * <p>
     * <em>Do not override this method.</em> Override {@link #render} to draw
     * non-atlas content. To declare atlas layers, call {@link #setSpriteLayers}
     * or override {@link #getActiveLayers}.
     */
    public final void renderComponent(int mouseX, int mouseY, float partialTicks) {
        syncSlotPositions();
        if (!isVisible()) return;

        boolean nowHovered = contains(mouseX, mouseY);
        if (nowHovered && !hovered) onMouseEnter();
        else if (!nowHovered && hovered) onMouseLeave();
        hovered = nowHovered;

        drawSpriteLayers();

        render(mouseX, mouseY, partialTicks);

        if (children.isEmpty()) return;
        for (Component child : children) {
            child.renderComponent(mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Renders the visual content of this component (non-atlas draws).
     * Atlas sprite layers are rendered automatically before this method is called.
     * Children are rendered automatically after this method returns.
     * <p>
     * Override this for custom GL draws, text, progress bars, etc.
     * To declare atlas layers, call {@link #setSpriteLayers} in the constructor
     * or override {@link #getActiveLayers} for state-driven layers.
     */
    protected abstract void render(int mouseX, int mouseY, float partialTicks);

    /**
     * Declares the ordered list of atlas sprite names to render bottom-to-top
     * at this component's bounds before {@link #render} is called.
     * Call this in your constructor for static layer configurations.
     * <p>Example:
     * <pre>{@code
     * // In constructor:
     * setSpriteLayers("button_bg");
     * }</pre>
     * For state-driven (hover/pressed) layer sets, override
     * {@link #getActiveLayers} instead.
     *
     * @param layers atlas base-names without extension; {@code null} entries
     *               are silently skipped at render time
     */
    protected final void setSpriteLayers(String... layers) {
        this.spriteLayers = layers != null ? layers : new String[0];
    }

    /**
     * Returns the sprite names to render this frame, in bottom-to-top order.
     * Override this to implement state-driven layer selection:
     * <pre>{@code
     * @Override
     * protected String[] getActiveLayers() {
     *     if (isPressed()) return new String[]{"button_bg", "button_pressed"};
     *     if (isHovered()) return new String[]{"button_bg", "button_hover"};
     *     return new String[]{"button_bg"};
     * }
     * }</pre>
     * The default implementation returns the array set via {@link #setSpriteLayers}.
     *
     * @return layer names; {@code null} entries are silently skipped
     */
    protected String[] getActiveLayers() {
        return spriteLayers;
    }

    // -------------------------------------------------------------------------
    // Tooltip
    // -------------------------------------------------------------------------

    /**
     * Draws all active sprite layers at this component's absolute bounds.
     * Binds the atlas texture once before the first layer draw.
     * Called automatically by {@link #renderComponent}; do not call manually.
     */
    private void drawSpriteLayers() {
        String[] layers = getActiveLayers();
        if (layers == null || layers.length == 0) return;

        ComponentAtlas atlas = ComponentAtlas.INSTANCE;
        if (!atlas.isReady()) return;

        atlas.bind(); // bind once for all layers
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        Tessellator tess = Tessellator.getInstance();

        for (String name : layers) {
            if (name == null) continue;
            AtlasRegion region = atlas.getRegion(name);
            if (region == null) continue;
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buf.pos(ax, ay + height, 0).tex(region.u0(), region.v1()).endVertex();
            buf.pos(ax + width, ay + height, 0).tex(region.u1(), region.v1()).endVertex();
            buf.pos(ax + width, ay, 0).tex(region.u1(), region.v0()).endVertex();
            buf.pos(ax, ay, 0).tex(region.u0(), region.v0()).endVertex();
            tess.draw();
        }
    }

    /**
     * Override to supply tooltip lines when this component is hovered.
     * <p>
     * The returned lines are NOT rendered by this component; the GUI collects
     * them via {@link #collectTooltip} and renders them centrally at the end of
     * the frame.
     *
     * @return tooltip lines, or {@code null} if no tooltip
     */
    @NotNull
    public List<String> getTooltip(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Event dispatch
    // -------------------------------------------------------------------------

    /**
     * Depth-first search (children highest-z-index first) for the first non-null
     * tooltip in the subtree that contains {@code (mouseX, mouseY)}.
     * <p>
     * Called by {@link CFNBaseGui}
     * to find which component should show its tooltip this frame.
     *
     * @return the winning tooltip, or {@code null} if none
     */
    @NotNull
    public final List<String> collectTooltip(int mouseX, int mouseY) {
        if (!isVisible() || !contains(mouseX, mouseY)) return Collections.emptyList();

        // Children are sorted ascending, so iterate backward for highest-z-index first
        for (int i = children.size() - 1; i >= 0; i--) {
            List<String> tip = children.get(i).collectTooltip(mouseX, mouseY);
            if (!tip.isEmpty()) return tip;
        }

        return getTooltip(mouseX, mouseY);
    }

    /**
     * Dispatches a mouse-click event through the component tree.
     * Children with the highest z-index are tried first; the first
     * {@link #onMouseClicked} that returns {@code true} consumes the event.
     *
     * @return {@code true} if the event was consumed
     */
    public final boolean dispatchMouseClicked(int mouseX, int mouseY, int button) {
        if (!isVisible() || !isEnabled() || !contains(mouseX, mouseY)) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).dispatchMouseClicked(mouseX, mouseY, button)) return true;
        }

        return onMouseClicked(mouseX, mouseY, button);
    }

    /**
     * Dispatches a mouse-release event through the component tree.
     * Same z-index priority and propagation rules as {@link #dispatchMouseClicked}.
     *
     * @return {@code true} if the event was consumed
     */
    public final boolean dispatchMouseReleased(int mouseX, int mouseY, int button) {
        if (!isVisible() || !isEnabled() || !contains(mouseX, mouseY)) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).dispatchMouseReleased(mouseX, mouseY, button)) return true;
        }

        return onMouseReleased(mouseX, mouseY, button);
    }

    /**
     * Dispatches a scroll-wheel event through the component tree.
     * Same z-index priority and propagation rules as {@link #dispatchMouseClicked}.
     *
     * @param delta {@code +1} for scroll-up, {@code -1} for scroll-down
     * @return {@code true} if the event was consumed
     */
    public final boolean dispatchMouseScrolled(int mouseX, int mouseY, int delta) {
        if (!isVisible() || !isEnabled() || !contains(mouseX, mouseY)) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).dispatchMouseScrolled(mouseX, mouseY, delta)) return true;
        }

        return onMouseScrolled(mouseX, mouseY, delta);
    }

    // -------------------------------------------------------------------------
    // Event hooks — override in sub-classes
    // -------------------------------------------------------------------------

    /**
     * Broadcasts a key-typed event to all enabled components in the subtree.
     * Unlike pointer events, this does NOT check {@link #contains} — key events
     * are delivered regardless of cursor position. The first component that returns
     * {@code true} stops further propagation.
     *
     * @return {@code true} if the event was consumed
     */
    public final boolean dispatchKeyTyped(char typedChar, int keyCode) {
        if (!isVisible() || !isEnabled()) return false;

        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).dispatchKeyTyped(typedChar, keyCode)) return true;
        }

        return onKeyTyped(typedChar, keyCode);
    }

    /**
     * Called when a mouse button is pressed inside this component's bounds
     * and all children have declined to consume the event.
     *
     * @return {@code true} to consume the event and stop propagation
     */
    protected boolean onMouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    /**
     * Called when a mouse button is released inside this component's bounds
     * and all children have declined to consume the event.
     *
     * @return {@code true} to consume the event and stop propagation
     */
    protected boolean onMouseReleased(int mouseX, int mouseY, int button) {
        return false;
    }

    /**
     * Called when the scroll wheel is used while the cursor is over this
     * component and all children have declined to consume the event.
     *
     * @param delta {@code +1} up, {@code -1} down
     * @return {@code true} to consume the event and stop propagation
     */
    protected boolean onMouseScrolled(int mouseX, int mouseY, int delta) {
        return false;
    }

    /**
     * Called when a key is typed and all children have declined to consume
     * the event. Key events bypass position checks.
     *
     * @return {@code true} to consume the event and stop propagation
     */
    protected boolean onKeyTyped(char typedChar, int keyCode) {
        return false;
    }

    /**
     * Fired once when the cursor enters this component's bounding rectangle.
     */
    protected void onMouseEnter() {
    }

    // -------------------------------------------------------------------------
    // Per-frame update
    // -------------------------------------------------------------------------

    /**
     * Fired once when the cursor leaves this component's bounding rectangle.
     */
    protected void onMouseLeave() {
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Called every screen tick (driven by {@code updateScreen}).
     * Override for animations, cursor blinking, or any time-driven state change.
     * The default implementation propagates the call to all children.
     */
    public void update() {
        for (Component child : children) {
            child.update();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        update = true;
        syncSlotPositions();
    }

    /**
     * Binds a {@link ComponentSlotLayout} to this component. The layout's slot positions
     * will be synced to this component's absolute coordinates every frame.
     * Call this from the GUI's {@code buildComponents()} method after the layout has
     * been registered into the container.
     */
    public void bindLayout(ComponentSlotLayout layout) {
        boundLayouts.add(layout);
        update = true;
        syncSlotPositions();
    }

    /**
     * Pushes the current absolute position (and visibility) to all bound layouts.
     * Called automatically at the start of each {@link #renderComponent} call and
     * whenever {@link #setVisible} is invoked.
     */
    public void syncSlotPositions() {
        if (!update && boundLayouts.isEmpty()) return;
        int ax = getAbsoluteX();
        int ay = getAbsoluteY();
        for (ComponentSlotLayout layout : boundLayouts) {
            layout.syncPositions(ax - gui.guiLeft, ay - gui.guiTop, isVisible());
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        update = true;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets this component's z-index and re-sorts the parent's (or root's) child list
     * so the new ordering takes effect immediately.
     */
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (parent != null) {
            parent.children.sort(Comparator.comparingInt(c -> c.zIndex));
        }
    }

    /**
     * The parent component, or {@code null} if this is a root-level component.
     */
    @Nullable
    public Component getParent() {
        return parent;
    }
}

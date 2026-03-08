package com.circulation.circulation_networks.gui;

import com.circulation.circulation_networks.container.CFNBaseContainer;
import com.circulation.circulation_networks.gui.component.base.AtlasRegion;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.ComponentAtlas;
import com.circulation.circulation_networks.gui.component.base.DraggableComponent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for all CFN GUI screens that need interactive components.
 * 所有需要交互式组件的 CFN GUI 界面的基类。
 *
 * <p><b>Rendering layers (bottom → top):</b>
 * <ol>
 *   <li><b>Background texture</b> — implement {@link #drawBG}.</li>
 *   <li><b>Slots &amp; item stacks</b> — handled automatically by {@link GuiContainer}.</li>
 *   <li><b>Foreground labels</b> — implement {@link #drawFG} for
 *       any static text rendered at GUI-relative coordinates.</li>
 *   <li><b>Component layer</b> — all {@link Component} instances registered via
 *       {@link #buildComponents}, sorted ascending by z-index.</li>
 *   <li><b>Component tooltip</b> — collected from the topmost hovered component.</li>
 *   <li><b>Slot tooltip</b> — the vanilla hover-text for inventory slots (rendered last,
 *       always on top).</li>
 * </ol>
 *
 * <p><b>Event routing:</b> Mouse and key events are delivered to components in descending
 * z-index order (highest z-index first). The first component that returns {@code true}
 * from its handler consumes the event; remaining components and the default
 * {@link GuiContainer} behaviour are skipped. If no component consumes the event,
 * {@code super} is called so that slot clicks, button presses, and Escape-to-close still
 * work as expected.
 *
 * <p><b>Dragging:</b> When a {@link DraggableComponent} starts dragging inside
 * {@link #mouseClicked}, this class finds it and stores it as the active drag target.
 * {@link DraggableComponent#handleDrag} is called every frame from {@link #drawScreen} so the component
 * tracks the mouse smoothly. On mouse release {@link DraggableComponent#stopDrag} is
 * called regardless of the cursor position, then the target is cleared.
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * public class MyGui extends CFNGuiContainer {
 *     public MyGui(MyContainer container) { super(container); }
 *
 *     @Override
 *     protected void buildComponents(List<Component> components) {
 *         components.add(new MyButton(guiLeft + 10, guiTop + 30, 60, 20));
 *     }
 *
 *     @Override
 *     public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
 *         // bind texture, drawTexturedModalRect(...)
 *     }
 *
 *     @Override
 *     public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
 *         // draw GUI labels in GUI-relative coordinates
 *     }
 * }
 * }</pre>
 *
 * <p><b>注意：</b>子类不应重写 {@code drawGuiContainerBackgroundLayer} 或
 * {@code drawGuiContainerForegroundLayer} —— 这两个方法均已标记 {@code final}，
 * 请改为实现 {@link #drawBG} 和 {@link #drawFG}。组件注册必须在覆写的
 * {@link #buildComponents(List)} 中进行。若需临时隐藏某个组件，
 * 请调用 {@link Component#setVisible(boolean)} 而非动态增删。
 * 同样，不应在 {@link #drawScreen} 中再次调用 {@code drawDefaultBackground()}
 * 或 {@code renderHoveredToolTip()} —— 基类已经统一处理。
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public abstract class CFNBaseGui extends GuiContainer {

    /**
     * Root-level components frozen after {@link #buildComponents}, sorted ascending
     * by z-index. An empty array until the first {@link #initGui} call.
     * 顶层组件数组，在 buildComponents 调用结束后冻结，按 z-index 升序排列。
     */
    private Component[] components = new Component[0];

    /**
     * The currently-dragged component, or {@code null} if no drag is active.
     */
    @Nullable
    private DraggableComponent dragTarget;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * @param container the dedicated container for this GUI; must be a {@link CFNBaseContainer}
     *                  subclass — generic {@code EmptyContainer} is not accepted here.
     */
    protected CFNBaseGui(@Nonnull CFNBaseContainer container) {
        super(container);
    }

    // -------------------------------------------------------------------------
    // Component registration
    // -------------------------------------------------------------------------

    /**
     * Walks the root component array depth-first and returns the first
     * {@link DraggableComponent} whose {@link DraggableComponent#isDragging()} is
     * {@code true}. Returns {@code null} if none is found.
     * 深度优先遍历顶层组件数组，返回第一个处于拖曳状态的 DraggableComponent，找不到则返回 null。
     */
    @Nullable
    private static DraggableComponent findDraggingComponent(Component[] nodes) {
        for (Component c : nodes) {
            if (c instanceof DraggableComponent && ((DraggableComponent) c).isDragging()) {
                return (DraggableComponent) c;
            }
            DraggableComponent found = findDraggingComponent(c.getChildren());
            if (found != null) return found;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // GUI lifecycle
    // -------------------------------------------------------------------------

    /**
     * Recursive descent into a child list. 递归进入子组件列表。
     */
    @Nullable
    private static DraggableComponent findDraggingComponent(List<Component> nodes) {
        for (Component c : nodes) {
            if (c instanceof DraggableComponent && ((DraggableComponent) c).isDragging()) {
                return (DraggableComponent) c;
            }
            DraggableComponent found = findDraggingComponent(c.getChildren());
            if (found != null) return found;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Atlas background rendering helper
    // -------------------------------------------------------------------------

    /**
     * Renders a UI background sprite from the shared {@link ComponentAtlas} at the
     * given screen-absolute position and size.
     *
     * <p>Background sprites are registered via
     * {@link com.circulation.circulation_networks.gui.component.base.RegisterComponentSpritesEvent#registerBackground}
     * and loaded from
     * {@code assets/circulation_networks/textures/gui/background/<name>.png}.
     *
     * <p>用法示例（在 {@link #drawBG} 中调用）：
     * <pre>{@code
     * drawAtlasBackground("furnace_bg", guiLeft, guiTop, xSize, ySize);
     * }</pre>
     *
     * @param name    背景精灵基名（不含 {@code .png} 后缀）
     * @param screenX 屏幕绝对 X 坐标（左边缘）
     * @param screenY 屏幕绝对 Y 坐标（上边缘）
     * @param w       渲染宽度（屏幕像素）
     * @param h       渲染高度（屏幕像素）
     */
    protected static void drawAtlasBackground(String name, int screenX, int screenY, int w, int h) {
        ComponentAtlas atlas = ComponentAtlas.INSTANCE;
        AtlasRegion region = atlas.getBackground(name);
        if (region == null) return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        atlas.bind();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(screenX,     screenY + h, 0).tex(region.u0(), region.v1()).endVertex();
        buf.pos(screenX + w, screenY + h, 0).tex(region.u1(), region.v1()).endVertex();
        buf.pos(screenX + w, screenY,     0).tex(region.u1(), region.v0()).endVertex();
        buf.pos(screenX,     screenY,     0).tex(region.u0(), region.v0()).endVertex();
        tess.draw();
    }

    // -------------------------------------------------------------------------
    // Abstract rendering hooks
    // -------------------------------------------------------------------------

    /**
     * Override this method to register all root-level components for this GUI.
     * This is the <em>only</em> place where components should be added; the list is
     * sorted by z-index and frozen into an array immediately after this method returns.
     * 覆写此方法以注册此 GUI 的所有顶层组件。这是添加组件的<b>唯一时机</b>；
     * 方法返回后，列表将立即按 z-index 排序并冻结为数组。
     *
     * <p>The {@code components} list must not be stored or used beyond this method's
     * scope — it is discarded after the array is built.
     * {@code components} 列表不应在此方法范围外保留引用，数组构建完毕后即被丢弃。
     *
     * <p>At call time, {@link #guiLeft}, {@link #guiTop}, {@link #width}, and
     * {@link #height} are already set by the parent {@link #initGui}, so layout
     * calculations using those values are safe.
     * 调用此方法时 guiLeft / guiTop / width / height 均已就绪，可安全用于布局计算。
     *
     * <p>To temporarily hide a component at runtime, call
     * {@link Component#setVisible(boolean)} rather than unregistering it.
     * 若需运行时临时隐藏某组件，请调用 {@code setVisible(false)} 而非尝试动态注销。
     *
     * @param components mutable list to add root-level components into
     *                   用于添加顶层组件的可变列表
     */
    protected List<Component> buildComponents(List<Component> components) {
        return components;
    }

    @Override
    public void initGui() {
        super.initGui();
        dragTarget = null;
        List<Component> list = buildComponents(new ObjectArrayList<>());
        list.sort(Comparator.comparingInt(Component::getZIndex));
        components = list.toArray(new Component[0]);
        ComponentAtlas.INSTANCE.awaitReady();
    }

    /**
     * Draw the GUI background texture. Called by the {@code final}
     * {@link #drawGuiContainerBackgroundLayer} override; subclasses must
     * implement this instead of overriding that method directly.
     * 绘制 GUI 背景贴图。由 final 的 drawGuiContainerBackgroundLayer 调用；
     * 子类必须实现此方法，而不是直接覆盖 drawGuiContainerBackgroundLayer。
     *
     * @param offsetX screen-absolute X of the GUI top-left corner (= {@code guiLeft})
     *                GUI 左上角在屏幕上的 X 坐标（即 guiLeft）
     * @param offsetY screen-absolute Y of the GUI top-left corner (= {@code guiTop})
     *                GUI 左上角在屏幕上的 Y 坐标（即 guiTop）
     * @param mouseX  screen-absolute mouse X / 鼠标屏幕 X 坐标
     * @param mouseY  screen-absolute mouse Y / 鼠标屏幕 Y 坐标
     */
    public abstract void drawBG(int offsetX, int offsetY, int mouseX, int mouseY);

    /**
     * Draw GUI foreground elements (labels, overlays, etc.). Called by the {@code final}
     * {@link #drawGuiContainerForegroundLayer} override; subclasses must
     * implement this instead of overriding that method directly.
     * 绘制 GUI 前景内容（标签、叠加层等）。由 final 的 drawGuiContainerForegroundLayer 调用；
     * 子类必须实现此方法而非直接覆盖 drawGuiContainerForegroundLayer。
     *
     * <p><b>Coordinate note:</b> when this method is called, the GL matrix has already
     * been translated to ({@code guiLeft}, {@code guiTop}) by
     * {@link GuiContainer#drawScreen}, so draw calls with GUI-relative coordinates
     * (e.g. {@code drawString(fr, text, 8, 6, color)}) are positioned correctly.
     * The {@code offsetX}/{@code offsetY} parameters echo {@code guiLeft}/{@code guiTop}
     * for any absolute-coordinate calculations that may be needed.
     * <p><b>坐标说明：</b>此方法调用时 GL 矩阵已平移到 GUI 左上角，可直接用相对坐标绘制内容。
     *
     * @param offsetX {@code guiLeft}
     * @param offsetY {@code guiTop}
     * @param mouseX  screen-absolute mouse X / 鼠标屏幕 X 坐标
     * @param mouseY  screen-absolute mouse Y / 鼠标屏幕 Y 坐标
     */
    public abstract void drawFG(int offsetX, int offsetY, int mouseX, int mouseY);

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    /**
     * Sealed entry point for the background layer.
     * Calls {@link #drawBG} with the GUI origin offset.
     * 背景层的封闭入口，将调用转发到 {@link #drawBG}。
     */
    @Override
    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        final int ox = this.guiLeft;
        final int oy = this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawBG(ox, oy, mouseX, mouseY);
    }

    /**
     * Sealed entry point for the foreground layer.
     * Calls {@link #drawFG} with the GUI origin offset.
     * The GL matrix is already translated to ({@code guiLeft}, {@code guiTop}) when
     * this method is invoked (handled by {@link GuiContainer#drawScreen}).
     * 前景层的封闭入口，将调用转发到 {@link #drawFG}。调用时 GL 矩阵已平移到 GUI 左上角。
     */
    @Override
    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        final int ox = this.guiLeft;
        final int oy = this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawFG(ox, oy, mouseX, mouseY);
    }

    // -------------------------------------------------------------------------
    // Event handling
    // -------------------------------------------------------------------------

    /**
     * Main render entry point. Draws all layers in the order documented on this class.
     * Subclasses must not override this method; use {@link #drawBG} and {@link #drawFG}
     * for texture / label rendering, and {@link #buildComponents} for interactive widgets.
     *
     * <p>主渲染入口。子类不应覆盖此方法；请通过 drawBG / drawFG 绘制贴图和标签，
     * 通过 buildComponents 注册交互组件。
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (dragTarget != null && dragTarget.isDragging()) {
            dragTarget.handleDrag(mouseX, mouseY);
        }

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        for (Component c : components) {
            c.renderComponent(mouseX, mouseY, partialTicks);
        }
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);

        List<String> componentTooltip = collectComponentTooltip(mouseX, mouseY);
        if (componentTooltip != null && !componentTooltip.isEmpty()) {
            drawHoveringText(componentTooltip, mouseX, mouseY);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Propagates the per-tick update to all registered components.
     * 将每帧更新传播给所有注册的组件。
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
        for (Component c : components) {
            c.update();
        }
    }

    /**
     * Dispatches mouse-click events to components (highest z-index first).
     * 将鼠标点击事件分发给各组件（z-index 最高者优先）。
     *
     * <p>If a component consumes the click, the component tree is scanned for any
     * {@link DraggableComponent} that started dragging, and it is stored as
     * {@link #dragTarget}. The {@code super} slot-interaction logic is skipped.
     *
     * <p>If no component consumes the click, {@code super.mouseClicked()} is called so
     * slot clicks and {@link net.minecraft.client.gui.GuiButton} presses still work.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchMouseClicked(mouseX, mouseY, mouseButton)) {
                // A component consumed the click; check if a drag was started.
                dragTarget = findDraggingComponent(components);
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Handles mouse-release events.
     * 处理鼠标释放事件。
     *
     * <p>If a drag is active, {@link DraggableComponent#stopDrag} is called and
     * {@code dragTarget} is cleared — regardless of the current cursor position.
     * Otherwise, the event is dispatched to components then to {@code super}.
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragTarget != null) {
            dragTarget.stopDrag(mouseX, mouseY);
            dragTarget = null;
            return;
        }
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchMouseReleased(mouseX, mouseY, state)) return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Dispatches key-typed events to components then falls back to {@code super}
     * (which handles Escape-to-close and other default bindings).
     * 将键盘输入事件分发给各组件，未消耗时回退到 super（处理 Esc 关闭等默认行为）。
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchKeyTyped(typedChar, keyCode)) return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Extends the base mouse-input handler with scroll-wheel dispatch.
     * 在基础鼠标输入处理上增加滚轮事件分发。
     *
     * <p>{@code super.handleMouseInput()} already handles button press/release by
     * calling {@link #mouseClicked} / {@link #mouseReleased}. After that, the scroll
     * delta ({@link Mouse#getEventDWheel()}) is checked and dispatched to components if
     * non-zero.
     */
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scrollDelta = Mouse.getEventDWheel();
        if (scrollDelta != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            int delta = scrollDelta > 0 ? 1 : -1;
            for (int i = components.length - 1; i >= 0; i--) {
                if (components[i].dispatchMouseScrolled(mouseX, mouseY, delta)) break;
            }
        }
    }

    /**
     * Collects the tooltip from the topmost (highest z-index) hovered component.
     * Iteration is in descending z-index order so that components visually on top
     * take priority.
     * 从 z-index 最高的悬浮组件收集提示文本，保证视觉上最顶层的组件优先。
     *
     * @return tooltip lines, or {@code null} if no hovered component has a tooltip
     */
    @Nonnull
    private List<String> collectComponentTooltip(int mouseX, int mouseY) {
        for (int i = components.length - 1; i >= 0; i--) {
            List<String> tip = components[i].collectTooltip(mouseX, mouseY);
            if (!tip.isEmpty()) return tip;
        }
        return Collections.emptyList();
    }
}

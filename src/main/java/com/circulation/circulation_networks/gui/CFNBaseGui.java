package com.circulation.circulation_networks.gui;

import com.circulation.circulation_networks.client.compat.GuiGraphicsCompat;
import com.circulation.circulation_networks.client.compat.RenderSystemCompat;
import com.circulation.circulation_networks.container.CFNBaseContainer;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.ComponentAtlas;
import com.circulation.circulation_networks.gui.component.base.ComponentScreenController;
import com.circulation.circulation_networks.gui.component.base.RenderPhase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all CFN GUI screens that need interactive components.
 *
 * <p>Rendering layers (bottom → top):
 * <ol>
 *   <li>Background texture — implement {@link #drawBG}.</li>
 *   <li>Component-owned slot and item rendering.</li>
 *   <li>Foreground labels — implement {@link #drawFG}.</li>
 *   <li>Component layer — all {@link Component} instances registered via
 *       {@link #buildComponents}, sorted ascending by z-index.</li>
 *   <li>Component tooltip — collected from the topmost hovered component.</li>
 * </ol>
 *
 * <p>Event routing: Mouse and key events are delivered to components in descending
 * z-index order (highest z-index first). The first component that returns {@code true}
 * from its handler consumes the event; remaining components and the default
 * behavior are skipped.
 */
@SuppressWarnings("unused")
public abstract class CFNBaseGui<T extends CFNBaseContainer> extends AbstractContainerScreen<T> {

    protected final T container;
    private final ComponentScreenController componentController = new ComponentScreenController();
    @Nullable
    protected Slot hoveredSlot;
    private float currentPartialTick;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    protected CFNBaseGui(@NotNull T container, Inventory playerInventory, net.minecraft.network.chat.Component title) {
        super(container, playerInventory, title);
        this.container = container;
    }

    protected CFNBaseGui(@NotNull T container, Inventory playerInventory, net.minecraft.network.chat.Component title, int imageWidth, int imageHeight) {
        super(container, playerInventory, title, imageWidth, imageHeight);
        this.container = container;
    }

    public int getGuiLeftPos() {
        return this.leftPos;
    }

    public int getGuiTopPos() {
        return this.topPos;
    }

    public int getGuiWidth() {
        return this.imageWidth;
    }

    public int getGuiHeight() {
        return this.imageHeight;
    }

    public void setHoveredSlot(@Nullable Slot hoveredSlot) {
        this.hoveredSlot = hoveredSlot;
    }

    public void focusComponentInput(@Nullable GuiEventListener listener) {
        this.setFocused(listener);
    }

    public void clearComponentInputFocus(@Nullable GuiEventListener listener) {
        if (this.getFocused() == listener) {
            this.setFocused(null);
        }
    }

    public List<String> getContainerItemTooltipLines(ItemStack stack) {
        List<net.minecraft.network.chat.Component> lines = getTooltipFromContainerItem(stack);
        if (lines.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<String> tooltip = new ObjectArrayList<>(lines.size());
        for (net.minecraft.network.chat.Component line : lines) {
            tooltip.add(line.getString());
        }
        return tooltip;
    }

    public boolean isTopComponent(Component component, int mouseX, int mouseY) {
        return componentController.getTopComponentAt(mouseX, mouseY) == component;
    }

    public void bringComponentToFront(Component component) {
        componentController.bringToFront(component);
    }

    // -------------------------------------------------------------------------
    // Component registration
    // -------------------------------------------------------------------------

    /**
     * Override this method to register all root-level components for this GUI.
     * Add components into the map under the appropriate {@link RenderPhase}.
     */
    protected void buildComponents(Map<RenderPhase, List<Component>> components) {
    }

    // -------------------------------------------------------------------------
    // GUI lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void init() {
        super.init();
        Map<RenderPhase, List<Component>> phaseMap = new EnumMap<>(RenderPhase.class);
        buildComponents(phaseMap);
        componentController.initializeComponents(phaseMap);
        ComponentAtlas.INSTANCE.awaitReady();
    }


    // -------------------------------------------------------------------------
    // Abstract rendering hooks
    // -------------------------------------------------------------------------

    /**
     * Draw the GUI background texture.
     */
    public abstract void drawBG(int offsetX, int offsetY, int mouseX, int mouseY);

    /**
     * Draw GUI foreground elements (labels, overlays, etc.).
     */
    public abstract void drawFG(int offsetX, int offsetY, int mouseX, int mouseY);

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    private void resetColor() {
        RenderSystemCompat.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderComponentPhase(GuiGraphicsExtractor guiGraphics, RenderPhase phase, int mouseX, int mouseY, float partialTicks) {
        resetGuiRenderState();
        resetColor();
        Component.setCurrentGuiGraphics(guiGraphics);
        try {
            componentController.renderPhase(phase, mouseX, mouseY, partialTicks);
        } finally {
            Component.setCurrentGuiGraphics(null);
        }
    }

    @Nullable
    private List<net.minecraft.network.chat.Component> collectComponentTooltip(int mouseX, int mouseY) {
        List<String> componentTooltip = componentController.collectTooltip(mouseX, mouseY);
        if (componentTooltip == null || componentTooltip.isEmpty()) {
            return null;
        }
        List<net.minecraft.network.chat.Component> mcTooltip = new ObjectArrayList<>();
        for (String line : componentTooltip) {
            mcTooltip.add(net.minecraft.network.chat.Component.literal(line));
        }
        return mcTooltip;
    }

    private void resetGuiRenderState() {
        RenderSystemCompat.enableBlend();
        RenderSystemCompat.defaultBlendFunc();
        RenderSystemCompat.disableDepthTest();
        RenderSystemCompat.disableCull();
        RenderSystemCompat.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        currentPartialTick = partialTicks;
        componentController.handleActiveDrag(mouseX, mouseY);
        this.hoveredSlot = null;
        super.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
        resetGuiRenderState();
        resetColor();
        this.drawFG(this.leftPos, this.topPos, mouseX, mouseY);
        renderComponentPhase(guiGraphics, RenderPhase.NORMAL, mouseX, mouseY, partialTicks);
        renderComponentPhase(guiGraphics, RenderPhase.FOREGROUND, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        GuiGraphicsCompat.pushPose(guiGraphics);
        GuiGraphicsCompat.translate(guiGraphics, -this.leftPos, -this.topPos, 0.0F);
        renderComponentPhase(guiGraphics, RenderPhase.BACKGROUND, mouseX, mouseY, currentPartialTick);
        resetColor();
        this.drawBG(this.leftPos, this.topPos, mouseX, mouseY);
        GuiGraphicsCompat.popPose(guiGraphics);
        super.extractLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void extractTooltip(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        List<net.minecraft.network.chat.Component> mcTooltip = collectComponentTooltip(mouseX, mouseY);
        if (mcTooltip != null && !mcTooltip.isEmpty()) {
            guiGraphics.setComponentTooltipForNextFrame(this.font, mcTooltip, mouseX, mouseY);
            return;
        }
        super.extractTooltip(guiGraphics, mouseX, mouseY);
    }


    // -------------------------------------------------------------------------
    // Event handling
    // -------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (componentController.mouseClicked((int) event.x(), (int) event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (componentController.mouseReleased((int) event.x(), (int) event.y(), event.button())) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (componentController.keyTyped((char) 0, event.key())) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (componentController.keyTyped((char) event.codepoint(), 0)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0) {
            int d = scrollY > 0 ? 1 : -1;
            componentController.mouseScrolled((int) mouseX, (int) mouseY, d);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

}

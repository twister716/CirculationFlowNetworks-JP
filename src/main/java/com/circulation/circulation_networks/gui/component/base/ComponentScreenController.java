package com.circulation.circulation_networks.gui.component.base;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ComponentScreenController {

    private Component[] components = new Component[0];
    @Nullable
    private DraggableComponent dragTarget;

    public void initializeComponents(List<Component> rootComponents) {
        dragTarget = null;
        List<Component> sorted = new ObjectArrayList<>(rootComponents);
        sorted.sort(Comparator.comparingInt(Component::getZIndex));
        components = sorted.toArray(new Component[0]);
    }

    public void handleActiveDrag(int mouseX, int mouseY) {
        if (dragTarget != null && dragTarget.isDragging()) {
            dragTarget.handleDrag(mouseX, mouseY);
        }
    }

    public void renderComponents(int mouseX, int mouseY, float partialTicks) {
        for (Component component : components) {
            component.renderComponent(mouseX, mouseY, partialTicks);
        }
    }

    public void updateComponents() {
        for (Component component : components) {
            component.update();
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchMouseClicked(mouseX, mouseY, mouseButton)) {
                dragTarget = ComponentTreeUtils.findDraggingComponent(components);
                return true;
            }
        }
        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int state) {
        if (dragTarget != null) {
            dragTarget.stopDrag(mouseX, mouseY);
            dragTarget = null;
            return true;
        }
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchMouseReleased(mouseX, mouseY, state)) {
                return true;
            }
        }
        return false;
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchKeyTyped(typedChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean mouseScrolled(int mouseX, int mouseY, int delta) {
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i].dispatchMouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return false;
    }

    public List<String> collectTooltip(int mouseX, int mouseY) {
        if (components.length == 0) {
            return Collections.emptyList();
        }
        return ComponentTreeUtils.collectTopTooltip(components, mouseX, mouseY);
    }
}
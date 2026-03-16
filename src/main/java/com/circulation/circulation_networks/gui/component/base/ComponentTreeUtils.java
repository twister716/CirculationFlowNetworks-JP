package com.circulation.circulation_networks.gui.component.base;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public final class ComponentTreeUtils {

    private ComponentTreeUtils() {
    }

    @Nullable
    public static DraggableComponent findDraggingComponent(Component[] nodes) {
        for (Component component : nodes) {
            if (component instanceof DraggableComponent && ((DraggableComponent) component).isDragging()) {
                return (DraggableComponent) component;
            }
            DraggableComponent found = findDraggingComponent(component.getChildren());
            if (found != null) return found;
        }
        return null;
    }

    @Nullable
    public static DraggableComponent findDraggingComponent(List<Component> nodes) {
        for (Component component : nodes) {
            if (component instanceof DraggableComponent && ((DraggableComponent) component).isDragging()) {
                return (DraggableComponent) component;
            }
            DraggableComponent found = findDraggingComponent(component.getChildren());
            if (found != null) return found;
        }
        return null;
    }

    public static List<String> collectTopTooltip(Component[] components, int mouseX, int mouseY) {
        for (int i = components.length - 1; i >= 0; i--) {
            List<String> tip = components[i].collectTooltip(mouseX, mouseY);
            if (!tip.isEmpty()) return tip;
        }
        return Collections.emptyList();
    }
}
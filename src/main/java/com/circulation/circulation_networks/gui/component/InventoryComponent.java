package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.container.ComponentSlotLayout;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.ComponentGuiContext;

public class InventoryComponent extends Component {

    public static final int WIDTH = 164;
    public static final int HEIGHT = 78;

    public InventoryComponent(int x, int y, ComponentGuiContext gui) {
        super(x, y, WIDTH, HEIGHT, gui);
        setSpriteLayers("inventory");
    }

    public InventoryComponent(int x, int y, ComponentSlotLayout layout, ComponentGuiContext gui) {
        this(x, y, gui);
        bindLayout(layout);
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
    }
}
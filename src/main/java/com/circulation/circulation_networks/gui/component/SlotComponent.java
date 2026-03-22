package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.container.ComponentSlotLayout;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.ComponentGuiContext;

public class SlotComponent extends Component {

    public static final int SIZE = 18;

    public SlotComponent(int x, int y, ComponentSlotLayout layout, String bgSprite, ComponentGuiContext gui) {
        super(x, y, SIZE, SIZE, gui);
        setSpriteLayers(bgSprite);
        bindLayout(layout);
    }

}
package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.container.ComponentSlotLayout;
import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.Component;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SlotComponent extends Component {

    public static final int SIZE = 18;

    public SlotComponent(int x, int y, ComponentSlotLayout layout, String bgSprite, CFNBaseGui gui) {
        super(x, y, SIZE, SIZE, gui);
        setSpriteLayers(bgSprite);
        bindLayout(layout);
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
    }
}


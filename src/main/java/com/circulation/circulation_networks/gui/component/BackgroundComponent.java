package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.Component;

import javax.annotation.Nonnull;

/**
 * A non-interactive component used solely for rendering a GUI background sprite.
 * It does not respond to any mouse or key events.
 */
@SuppressWarnings("unused")
public class BackgroundComponent extends Component {

    public BackgroundComponent(@Nonnull String bgSprite, @Nonnull CFNBaseGui<?> gui) {
        this(gui.getXSize(), gui.getYSize(), bgSprite, gui);
    }

    public BackgroundComponent(int width, int height, @Nonnull String bgSprite, @Nonnull CFNBaseGui<?> gui) {
        super(0, 0, width, height, gui);
        setSpriteLayers("bg/" + bgSprite);
    }

    @Override
    public boolean contains(int mouseX, int mouseY) {
        return false;
    }
}
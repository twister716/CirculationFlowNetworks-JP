package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.ComponentAtlas;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class TriStateButtonComponent extends AbstractButtonComponent {

    private static final String[] EMPTY_LAYERS = new String[0];

    private final String inactiveSprite;
    private final String activeSprite;
    private final String pressedSprite;
    private final String[] cache = new String[1];

    private boolean active = false;
    private BooleanSupplier activeSupplier;

    public TriStateButtonComponent(int x, int y, int width, int height, CFNBaseGui<?> gui,
                                   String sprite, Runnable run) {
        super(x, y, width, height, gui, run);
        this.inactiveSprite = sprite;
        this.activeSprite = sprite + "_active";
        this.pressedSprite = sprite + "_pressed";
    }

    public boolean isActive() {
        return activeSupplier != null ? activeSupplier.getAsBoolean() : active;
    }

    public TriStateButtonComponent setActive(boolean active) {
        this.active = active;
        return this;
    }

    public TriStateButtonComponent setActiveSupplier(BooleanSupplier activeSupplier) {
        this.activeSupplier = activeSupplier;
        return this;
    }

    @Override
    protected String[] getActiveLayers() {
        ComponentAtlas atlas = ComponentAtlas.INSTANCE;

        String stateSprite;
        if (isPressed()) {
            stateSprite = pressedSprite;
        } else {
            stateSprite = isActive() ? activeSprite : inactiveSprite;
        }

        if (stateSprite != null && atlas.getRegion(stateSprite) != null) {
            cache[0] = stateSprite;
            return cache;
        }

        String fallbackSprite = isActive() ? activeSprite : inactiveSprite;
        if (fallbackSprite != null && atlas.getRegion(fallbackSprite) != null) {
            cache[0] = fallbackSprite;
            return cache;
        }

        return EMPTY_LAYERS;
    }
}
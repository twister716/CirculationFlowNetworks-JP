package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.ComponentAtlas;

import java.util.function.Supplier;

public class DynamicButtonComponent extends AbstractButtonComponent {

    private static final String[] EMPTY_LAYERS = new String[0];

    private final Supplier<String> spriteSupplier;
    private final String[] cache = new String[1];

    public DynamicButtonComponent(int x, int y, int width, int height, CFNBaseGui<?> gui, Supplier<String> spriteSupplier, Runnable run) {
        super(x, y, width, height, gui, run);
        this.spriteSupplier = spriteSupplier;
    }

    @Override
    protected String[] getActiveLayers() {
        String baseSprite = spriteSupplier != null ? spriteSupplier.get() : null;
        if (baseSprite == null || baseSprite.isEmpty()) {
            return EMPTY_LAYERS;
        }

        ComponentAtlas atlas = ComponentAtlas.INSTANCE;
        String sprite = baseSprite;
        if (!isEnabled()) {
            String disabled = baseSprite + "_disabled";
            if (atlas.getRegion(disabled) != null) {
                sprite = disabled;
            }
        } else if (isPressed()) {
            String pressed = baseSprite + "_pressed";
            if (atlas.getRegion(pressed) != null) {
                sprite = pressed;
            }
        } else if (isHovered()) {
            String hovered = baseSprite + "_hovered";
            if (atlas.getRegion(hovered) != null) {
                sprite = hovered;
            }
        }

        cache[0] = sprite;
        return cache;
    }
}

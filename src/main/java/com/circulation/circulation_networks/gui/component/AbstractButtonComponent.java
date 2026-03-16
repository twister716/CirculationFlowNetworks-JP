package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.ComponentGuiContext;

@SuppressWarnings("unused")
public abstract class AbstractButtonComponent extends Component {

    protected final String[] cache = new String[2];
    private final String normalSprite;
    private String hoveredSprite;
    private String pressedSprite;
    private String disabledSprite;
    private String iconNormal;
    private String iconHovered;
    private String iconPressed;
    private String iconDisabled;
    private boolean pressed = false;

    protected AbstractButtonComponent(int x, int y, int width, int height, ComponentGuiContext gui, String normalSprite) {
        super(x, y, width, height, gui);
        this.normalSprite = normalSprite;
    }

    public boolean isPressed() {
        return pressed;
    }

    public AbstractButtonComponent setHoveredSprite(String hoveredSprite) {
        this.hoveredSprite = hoveredSprite;
        return this;
    }

    public AbstractButtonComponent setPressedSprite(String pressedSprite) {
        this.pressedSprite = pressedSprite;
        return this;
    }

    public AbstractButtonComponent setDisabledSprite(String disabledSprite) {
        this.disabledSprite = disabledSprite;
        return this;
    }

    public AbstractButtonComponent setIconNormal(String iconNormal) {
        this.iconNormal = iconNormal;
        return this;
    }

    public AbstractButtonComponent setIconHovered(String iconHovered) {
        this.iconHovered = iconHovered;
        return this;
    }

    public AbstractButtonComponent setIconPressed(String iconPressed) {
        this.iconPressed = iconPressed;
        return this;
    }

    public AbstractButtonComponent setIconDisabled(String iconDisabled) {
        this.iconDisabled = iconDisabled;
        return this;
    }

    public AbstractButtonComponent setPressed(boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    @Override
    protected String[] getActiveLayers() {
        String bg;
        if (!isEnabled()) bg = disabledSprite != null ? disabledSprite : normalSprite;
        else if (pressed) bg = pressedSprite != null ? pressedSprite : normalSprite;
        else if (isHovered()) bg = hoveredSprite != null ? hoveredSprite : normalSprite;
        else bg = normalSprite;
        cache[0] = bg;

        String icon;
        if (iconNormal != null) {
            if (!isEnabled()) icon = iconDisabled != null ? iconDisabled : iconNormal;
            else if (pressed) icon = iconPressed != null ? iconPressed : iconNormal;
            else if (isHovered()) icon = iconHovered != null ? iconHovered : iconNormal;
            else icon = iconNormal;
            cache[1] = icon;
        } else cache[1] = null;

        return cache;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected final boolean onMouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            pressed = true;
            return true;
        }
        return false;
    }

    @Override
    protected final boolean onMouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0 && pressed) {
            pressed = false;
            onClick();
            return true;
        }
        pressed = false;
        return false;
    }

    @Override
    protected void onMouseLeave() {
        pressed = false;
    }

    protected abstract void onClick();
}
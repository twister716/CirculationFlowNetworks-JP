package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.RegisterComponentSpritesEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Abstract base for all clickable button components.
 *
 * <p>Handles four visual states driven by {@link #getActiveLayers()}:
 * <ol>
 *   <li><b>disabled</b> — {@link #isEnabled()} is {@code false}; click events are suppressed
 *       by {@link Component#dispatchMouseClicked}, rendering proceeds normally
 *   <li><b>pressed</b>  — left mouse button is held down over the component
 *   <li><b>hovered</b>  — cursor is inside the bounds (but not pressed)
 *   <li><b>normal</b>   — default state
 * </ol>
 *
 * <p>Each state sprite is optional except {@code normalSprite}. When a state sprite is
 * not set, the component falls back to {@code normalSprite}. The same opt-in rule applies
 * to the per-state icon sprites: only set icon states you have art for.
 *
 * <p>The constructor only requires {@code normalSprite}. All other sprites are
 * configured via fluent setters:
 * <pre>{@code
 * new MyButton(x, y, 20, 20, "btn_normal")
 *         .setHoveredSprite("btn_hovered")
 *         .setPressedSprite("btn_pressed")
 *         .setIconNormal("icon_wrench");
 * }</pre>
 *
 * <p>Subclasses must implement {@link #onClick()}.
 *
 * <p>All sprite names refer to atlas sprites registered via
 * {@link RegisterComponentSpritesEvent}.
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
@Setter
@Accessors(chain = true)
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
    @Getter
    private boolean pressed = false;

    protected AbstractButtonComponent(int x, int y, int width, int height, CFNBaseGui gui, String normalSprite) {
        super(x, y, width, height, gui);
        this.normalSprite = normalSprite;
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

    /**
     * Called once when the player completes a left-click (press + release) over
     * this button while it is enabled.
     */
    protected abstract void onClick();
}
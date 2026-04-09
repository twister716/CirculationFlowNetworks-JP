package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.gui.component.base.ComponentAtlas;
import com.circulation.circulation_networks.gui.component.base.DraggableComponent;

import javax.annotation.Nullable;

public class HorizontalSliderComponent extends Component {

    public interface HorizontalSliderParent {
        void onSliderChanged(HorizontalSliderComponent slider);
    }

    private static final int MIN = 0;

    private final HorizontalSliderParent sliderParent;
    private final SliderKnobComponent button;
    private int max;
    private int value;
    private int step = 1;

    public HorizontalSliderComponent(HorizontalSliderParent parent,
                                     int x,
                                     int y,
                                     int trackWidth,
                                     int trackHeight,
                                     int max,
                                     int initialValue,
                                     int buttonSize,
                                     String normalSprite,
                                     @Nullable String pressedSprite,
                                     CFNBaseGui<?> gui) {
        super(x, y, trackWidth, trackHeight, gui);
        if (max < 0) {
            throw new IllegalArgumentException("max must be greater than or equal to 0");
        }
        this.sliderParent = parent;
        this.max = max;
        this.button = new SliderKnobComponent(0, 0, buttonSize, buttonSize, normalSprite, pressedSprite, gui);
        addChild(button);
        updateButtonBounds();
        button.setEnabled(this.max > MIN && isEnabled());
        setValueInternal(initialValue, false);
    }

    public int getValue() {
        return value;
    }

    public int getMax() {
        return max;
    }

    public HorizontalSliderComponent setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max must be greater than or equal to 0");
        }
        this.max = max;
        updateButtonBounds();
        button.setEnabled(this.max > MIN && isEnabled());
        setValueInternal(value, false);
        return this;
    }

    public HorizontalSliderComponent setValue(int value) {
        setValueInternal(value, false);
        return this;
    }

    public int getScrollStep() {
        return step;
    }

    public HorizontalSliderComponent setScrollStep(int step) {
        this.step = Math.max(1, step);
        return this;
    }

    public boolean isDragging() {
        return button.isDragging();
    }

    public boolean scroll(int delta) {
        if (!isEnabled() || delta == 0 || max == MIN) {
            return false;
        }
        int direction = delta > 0 ? -1 : 1;
        int target = value + direction * step;
        return setValueInternal(target, true);
    }

    @Override
    public Component setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        button.setEnabled(enabled && max > MIN);
        return this;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, int delta) {
        return scroll(delta);
    }

    void onButtonDragged(boolean released) {
        syncValueFromButton();
        if (released) {
            syncButtonToValue();
            sliderParent.onSliderChanged(this);
        }
    }

    private boolean setValueInternal(int newValue, boolean notify) {
        if (max == MIN) {
            int old = value;
            value = MIN;
            syncButtonToValue();
            if (notify && old != value) {
                sliderParent.onSliderChanged(this);
            }
            return false;
        }

        int clamped = clampValue(newValue);
        if (value == clamped) {
            if (notify) {
                sliderParent.onSliderChanged(this);
            }
            return false;
        }

        value = clamped;
        syncButtonToValue();
        if (notify) {
            sliderParent.onSliderChanged(this);
        }
        return true;
    }

    private void updateButtonBounds() {
        int minX = 0;
        int maxX = Math.max(0, width - button.width);
        if (max == MIN) {
            button.setDragBounds(new int[]{minX, 0, minX, 0});
        } else {
            button.setDragBounds(new int[]{minX, 0, maxX, 0});
        }
    }

    private void syncButtonToValue() {
        button.setX(buttonXForValue(value));
        button.setY(0);
    }

    private void syncValueFromButton() {
        int x = clamp(button.x, minButtonX(), maxButtonX());
        if (button.x != x) {
            button.setX(x);
        }
        value = valueForButtonX(x);
    }

    private int valueForButtonX(int buttonX) {
        if (max == MIN) {
            return MIN;
        }
        int travel = travel();
        if (travel <= 0) {
            return MIN;
        }
        float t = (buttonX - minButtonX()) / (float) travel;
        int mapped = MIN + Math.round(t * (max - MIN));
        return clampValue(mapped);
    }

    private int buttonXForValue(int value) {
        int travel = travel();
        if (travel <= 0 || max == MIN) {
            return minButtonX();
        }
        int clamped = clampValue(value);
        float t = (clamped - MIN) / (float) (max - MIN);
        int mapped = minButtonX() + Math.round(t * travel);
        return clamp(mapped, minButtonX(), maxButtonX());
    }

    private int travel() {
        return Math.max(0, maxButtonX() - minButtonX());
    }

    private int minButtonX() {
        return 0;
    }

    private int maxButtonX() {
        return Math.max(minButtonX(), width - button.width);
    }

    private int clampValue(int value) {
        return clamp(value, MIN, max);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private final class SliderKnobComponent extends DraggableComponent {
        private final String normalSprite;
        private final String pressedSprite;
        private final String[] cache = new String[1];

        private SliderKnobComponent(int x, int y, int width, int height, String normalSprite, @Nullable String pressedSprite, CFNBaseGui<?> gui) {
            super(x, y, width, height, gui);
            this.normalSprite = normalSprite;
            this.pressedSprite = pressedSprite;
        }

        @Override
        protected String[] getActiveLayers() {
            ComponentAtlas atlas = ComponentAtlas.INSTANCE;
            if (!isEnabled()) {
                cache[0] = normalSprite;
            } else if (isDragging()) {
                if (pressedSprite != null && atlas.getRegion(pressedSprite) != null) {
                    cache[0] = pressedSprite;
                } else {
                    cache[0] = normalSprite;
                }
            } else {
                cache[0] = normalSprite;
            }
            return cache;
        }

        @Override
        protected void onDrag(int deltaX, int deltaY) {
            if (y != 0) {
                setY(0);
            }
            HorizontalSliderComponent.this.onButtonDragged(false);
        }

        @Override
        protected void onDragEnd(int mouseX, int mouseY) {
            if (y != 0) {
                setY(0);
            }
            HorizontalSliderComponent.this.onButtonDragged(true);
        }
    }
}

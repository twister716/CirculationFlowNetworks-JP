package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.client.compat.FontCompat;
import com.circulation.circulation_networks.container.ContainerCirculationShielder;
import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.Component;
import com.circulation.circulation_networks.packets.CirculationShielderSyncPacket;
import com.circulation.circulation_networks.tiles.BlockEntityCirculationShielder;
import com.circulation.circulation_networks.tooltip.LocalizedComponent;
import com.circulation.circulation_networks.utils.CI18n;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CirculationShielderPanelComponent extends Component implements HorizontalSliderComponent.HorizontalSliderParent {

    private static final int GUI_WIDTH = 138;
    private static final int GUI_HEIGHT = 56;
    private static final int SLIDER_PANEL_X = 8;
    private static final int SLIDER_PANEL_Y = 8;
    private static final int SLIDER_PANEL_W = 103;
    private static final int SLIDER_PANEL_H = 37;
    private static final int SLIDER_TEXT_X = 4;
    private static final int SLIDER_TEXT_Y = 4;
    private static final int SLIDER_TEXT_W = 95;
    private static final int SLIDER_TEXT_H = 10;
    private static final int SLIDER_TRACK_X = 3;
    private static final int SLIDER_TRACK_Y = 22;
    private static final int SLIDER_TRACK_W = 97;
    private static final int SLIDER_TRACK_H = 12;
    private static final int SLIDER_BUTTON_SIZE = 12;
    private static final int BUTTON_SIZE = 18;
    private static final int SHOW_RANGE_X = 112;
    private static final int SHOW_RANGE_Y = 8;
    private static final int REDSTONE_X = 112;
    private static final int REDSTONE_Y = 27;

    private final ContainerCirculationShielder container;
    private final BlockEntityCirculationShielder shielder;
    private final HorizontalSliderComponent slider;
    private final ButtonComponent redstoneOff;
    private final ButtonComponent redstoneOffRe;
    private final ButtonComponent redstoneOn;
    private final ButtonComponent redstoneOnRe;
    private int displayScope;
    private boolean lastRedstoneMode;
    private boolean lastPowered;

    public CirculationShielderPanelComponent(ContainerCirculationShielder container, BlockEntityCirculationShielder shielder, CFNBaseGui<?> gui) {
        super(0, 0, GUI_WIDTH, GUI_HEIGHT, gui);
        this.container = container;
        this.shielder = shielder;

        Component sliderPanel = new Component(SLIDER_PANEL_X, SLIDER_PANEL_Y, SLIDER_PANEL_W, SLIDER_PANEL_H, gui)
            .setSpriteLayers("shielder_slider");

        int textY = alignTextY(SLIDER_TEXT_Y, SLIDER_TEXT_H);
        TextComponent scopeText = new TextComponent(SLIDER_TEXT_X, textY, gui, this::getScopeText, 0xFFFFFF);
        sliderPanel.addChild(scopeText);

        int maxScope = getMaxScope();
        int initialScope = clampScope(container.scope, maxScope);
        this.displayScope = initialScope;
        this.slider = new HorizontalSliderComponent(
            this,
            SLIDER_TRACK_X,
            SLIDER_TRACK_Y,
            SLIDER_TRACK_W,
            SLIDER_TRACK_H,
            maxScope,
            initialScope,
            SLIDER_BUTTON_SIZE,
            "shielder_slider_button",
            "shielder_slider_button_pressed",
            gui
        );
        sliderPanel.addChild(slider);
        addChild(sliderPanel);

        DynamicButtonComponent showRangeToggle = new DynamicButtonComponent(SHOW_RANGE_X, SHOW_RANGE_Y, BUTTON_SIZE, BUTTON_SIZE, gui, this::getShowRangeSprite, this::toggleShowRange) {
            @Override
            protected @NotNull List<LocalizedComponent> getTooltip(int mouseX, int mouseY) {
                return buildShowRangeTooltip();
            }
        };
        addChild(showRangeToggle);

        redstoneOff = createRedstoneButton("shielder_redstone_off", true);
        redstoneOffRe = createRedstoneButton("shielder_redstone_off_re", true);
        redstoneOn = createRedstoneButton("shielder_redstone_on", false);
        redstoneOnRe = createRedstoneButton("shielder_redstone_on_re", false);
        addChild(redstoneOff, redstoneOffRe, redstoneOn, redstoneOnRe);

        lastRedstoneMode = shielder != null && shielder.getRedstoneMode();
        lastPowered = isRedstonePowered();
        syncToggleButtons(true);
    }

    private static int clampScope(int value, int maxScope) {
        if (maxScope < 0) {
            return 0;
        }
        return Math.clamp(value, 0, maxScope);
    }

    private static String trimToWidth(String text, int width) {
        return FontCompat.trimToWidth(text, width);
    }

    @Override
    public void update() {
        super.update();
        int maxScope = getMaxScope();
        if (slider.getMax() != maxScope) {
            slider.setMax(maxScope);
        }
        int serverScope = clampScope(container.scope, maxScope);
        if (!slider.isDragging() && slider.getValue() != serverScope) {
            slider.setValue(serverScope);
        }
        int sliderValue = clampScope(slider.getValue(), maxScope);
        if (displayScope != sliderValue) {
            displayScope = sliderValue;
            if (shielder != null && shielder.getScope() != sliderValue) {
                shielder.setScope(sliderValue);
            }
        }
        syncToggleButtons(false);
    }

    @Override
    public void onSliderChanged(HorizontalSliderComponent slider) {
        int maxScope = getMaxScope();
        int value = clampScope(slider.getValue(), maxScope);
        if (container.scope != value) {
            container.scope = value;
        }
        if (shielder != null && shielder.getScope() != value) {
            shielder.setScope(value);
        }
        if (shielder != null) {
            CirculationFlowNetworks.sendToServer(new CirculationShielderSyncPacket(shielder));
        }
    }

    private void toggleShowRange() {
        if (shielder == null) {
            return;
        }
        shielder.setShowingRange(!shielder.isShowingRange());
    }

    private void setRedstoneMode(boolean mode) {
        if (shielder == null) {
            return;
        }
        if (shielder.getRedstoneMode() == mode) {
            return;
        }
        shielder.setRedstoneMode(mode);
        CirculationFlowNetworks.sendToServer(new CirculationShielderSyncPacket(shielder));
        syncToggleButtons(true);
    }

    private void syncToggleButtons(boolean force) {
        boolean redstoneMode = shielder != null && shielder.getRedstoneMode();
        boolean powered = isRedstonePowered();

        if (force || redstoneMode != lastRedstoneMode || powered != lastPowered) {
            redstoneOff.setVisible(!redstoneMode && !powered);
            redstoneOffRe.setVisible(!redstoneMode && powered);
            redstoneOn.setVisible(redstoneMode && !powered);
            redstoneOnRe.setVisible(redstoneMode && powered);
            lastRedstoneMode = redstoneMode;
            lastPowered = powered;
        }
    }

    private boolean isRedstonePowered() {
        if (shielder == null) {
            return false;
        }
        return shielder.isReceivingRedstoneSignal();
    }

    private String getScopeText() {
        String label = CI18n.format("gui.circulation_shielder.scope");
        String text = label + ": " + displayScope;
        return trimToWidth(text, SLIDER_TEXT_W);
    }

    private String getShowRangeSprite() {
        return shielder != null && shielder.isShowingRange() ? "shielder_scope_on" : "shielder_scope_off";
    }

    private ButtonComponent createRedstoneButton(String sprite, boolean nextMode) {
        return new ButtonComponent(REDSTONE_X, REDSTONE_Y, BUTTON_SIZE, BUTTON_SIZE, gui, sprite, () -> setRedstoneMode(nextMode)) {
            @Override
            protected @NotNull List<LocalizedComponent> getTooltip(int mouseX, int mouseY) {
                return buildRedstoneTooltip();
            }
        };
    }

    private List<LocalizedComponent> buildShowRangeTooltip() {
        List<LocalizedComponent> tips = new ObjectArrayList<>(1);
        tips.add(LocalizedComponent.literal(boldText("gui.circulation_shielder.show_range")));
        return tips;
    }

    private List<LocalizedComponent> buildRedstoneTooltip() {
        boolean inverse = shielder != null && shielder.getRedstoneMode();
        List<LocalizedComponent> tips = new ObjectArrayList<>(2);
        tips.add(LocalizedComponent.literal(boldText(inverse ? "gui.circulation_shielder.inverse_mode" : "gui.circulation_shielder.normal_mode")));
        tips.add(LocalizedComponent.description(inverse ? "gui.circulation_shielder.inverse_desc" : "gui.circulation_shielder.normal_desc"));
        return tips;
    }

    private String boldText(String key) {
        return "§l" + CI18n.format(key);
    }

    private int getMaxScope() {
        return Math.max(0, container.maxScope);
    }
}

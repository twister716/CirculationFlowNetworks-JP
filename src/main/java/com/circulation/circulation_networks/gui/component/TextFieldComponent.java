package com.circulation.circulation_networks.gui.component;

import com.circulation.circulation_networks.gui.CFNBaseGui;
import com.circulation.circulation_networks.gui.component.base.Component;
import net.minecraft.client.Minecraft;
//? if <1.20 {
import net.minecraft.client.gui.GuiTextField;
//?} else {
/*import net.minecraft.client.gui.components.EditBox;
 *///?}

import javax.annotation.Nullable;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class TextFieldComponent extends Component {

    private static final Predicate<Character> ALLOW_ALL_INPUT = ignored -> true;
    private static int nextId = 1;

    //? if <1.20 {
    private final GuiTextField textField;
    //?} else {
    /*private EditBox textField;
     *///?}
    private Predicate<Character> inputAllowed = ALLOW_ALL_INPUT;
    private int maxLength;
    @Nullable
    private Boolean backgroundDrawing;

    public TextFieldComponent(int x, int y, int width, int height, CFNBaseGui<?> gui) {
        this(x, y, width, height, gui, 10, null);
    }

    public TextFieldComponent(int x, int y, int width, int height, CFNBaseGui<?> gui, int maxLength) {
        this(x, y, width, height, gui, maxLength, null);
    }

    public TextFieldComponent(int x, int y, int width, int height, CFNBaseGui<?> gui, int maxLength, @Nullable Boolean backgroundDrawing) {
        super(x, y, width, height, gui);
        this.backgroundDrawing = backgroundDrawing;
        this.maxLength = Math.max(0, maxLength);
        //? if <1.20 {
        this.textField = new GuiTextField(nextId++, Minecraft.getMinecraft().fontRenderer, getAbsoluteX(), getAbsoluteY(), width, height);
        this.textField.setMaxStringLength(this.maxLength);
        applyNativeState();
        //?} else {
        /*this.textField = createEditBox(this.maxLength);
        applyNativeState();
        *///?}
    }

    private static boolean shouldFilterCharacter(char typedChar) {
        return typedChar != 0 && !Character.isISOControl(typedChar);
    }

    public String getText() {
        //? if <1.20 {
        return textField.getText();
        //?} else {
        /*return textField.getValue();
         *///?}
    }

    public TextFieldComponent setText(String text) {
        //? if <1.20 {
        textField.setText(text != null ? text : "");
        //?} else {
        /*textField.setValue(text != null ? text : "");
         *///?}
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public TextFieldComponent setMaxLength(int maxLength) {
        this.maxLength = Math.max(0, maxLength);
        //? if <1.20 {
        textField.setMaxStringLength(this.maxLength);
        //?} else {
        /*textField.setMaxLength(this.maxLength);
         *///?}
        return this;
    }

    public boolean isFocused() {
        //? if <1.20 {
        return textField.isFocused();
        //?} else {
        /*return textField.isFocused();
         *///?}
    }

    public TextFieldComponent setFocused(boolean focused) {
        //? if <1.20 {
        textField.setFocused(focused);
        //?} else {
        /*textField.setFocused(focused);
         *///?}
        return this;
    }

    public Predicate<Character> getInputAllowed() {
        return inputAllowed;
    }

    public TextFieldComponent setInputAllowed(Predicate<Character> inputAllowed) {
        this.inputAllowed = inputAllowed != null ? inputAllowed : ALLOW_ALL_INPUT;
        return this;
    }

    private boolean isCharacterInputAllowed(char typedChar) {
        return !shouldFilterCharacter(typedChar) || inputAllowed.test(typedChar);
    }

    @Nullable
    public Boolean getBackgroundDrawing() {
        return backgroundDrawing;
    }

    public TextFieldComponent setBackgroundDrawing(@Nullable Boolean backgroundDrawing) {
        this.backgroundDrawing = backgroundDrawing;
        //? if <1.20 {
        applyNativeState();
        //?} else {
        /*applyNativeState();
         *///?}
        return this;
    }

    @Override
    protected void render(int mouseX, int mouseY, float partialTicks) {
        //? if <1.20 {
        syncTextFieldBounds();
        applyNativeState();
        restoreGuiRenderState();
        textField.drawTextBox();
        restoreGuiRenderState();
        //?} else {
        /*syncTextFieldBounds();
        applyNativeState();
        var guiGraphics = getCurrentGuiGraphics();
        if (guiGraphics != null) {
            textField.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        }
        *///?}
    }

    @Override
    public void update() {
        //? if <1.20 {
        syncTextFieldBounds();
        applyNativeState();
        textField.updateCursorCounter();
        //?} else {
        /*syncTextFieldBounds();
        applyNativeState();
        //? if <1.21 {
        textField.tick();
        //?}
        *///?}
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int button) {
        //? if <1.20 {
        syncTextFieldBounds();
        applyNativeState();
        return textField.mouseClicked(mouseX, mouseY, button);
        //?} else {
        /*syncTextFieldBounds();
        applyNativeState();
        return textField.mouseClicked(mouseX, mouseY, button);
        *///?}
    }

    @Override
    protected boolean onKeyTyped(char typedChar, int keyCode) {
        //? if <1.20 {
        if (!isCharacterInputAllowed(typedChar)) {
            return false;
        }
        applyNativeState();
        return textField.textboxKeyTyped(typedChar, keyCode);
        //?} else {
        /*if (!isCharacterInputAllowed(typedChar)) {
            return false;
        }
        applyNativeState();
        if (typedChar != 0 && textField.charTyped(typedChar, 0)) {
            return true;
        }
        return keyCode != 0 && textField.keyPressed(keyCode, 0, 0);
        *///?}
    }

    @Override
    protected void onGlobalMouseClicked(int mouseX, int mouseY, int button) {
        //? if <1.20 {
        if (!textField.isFocused() || super.contains(mouseX, mouseY)) {
            return;
        }
        syncTextFieldBounds();
        textField.mouseClicked(mouseX, mouseY, button);
        //?} else {
        /*if (!textField.isFocused() || super.contains(mouseX, mouseY)) {
            return;
        }
        syncTextFieldBounds();
        textField.mouseClicked(mouseX, mouseY, button);
        *///?}
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        //? if <1.20 {
        syncTextFieldBounds();
        //?} else {
        /*rebuildEditBox();
         *///?}
    }

    //? if <1.20 {
    private void syncTextFieldBounds() {
        textField.x = getAbsoluteX();
        textField.y = getAbsoluteY();
        textField.width = width;
        textField.height = height;
    }

    private void applyNativeState() {
        textField.setEnabled(isEnabled());
        textField.setVisible(isVisible());
        if (backgroundDrawing != null) {
            textField.setEnableBackgroundDrawing(backgroundDrawing);
        }
    }
    //?} else {
    /*private EditBox createEditBox(int maxLength) {
        EditBox field = new EditBox(
            Minecraft.getInstance().font,
            getAbsoluteX(),
            getAbsoluteY(),
            width,
            height,
            net.minecraft.network.chat.Component.literal("")
        );
        field.setCanLoseFocus(true);
        field.setMaxLength(Math.max(0, maxLength));
        return field;
    }

    private void rebuildEditBox() {
        String text = textField != null ? textField.getValue() : "";
        boolean focused = textField != null && textField.isFocused();
        textField = createEditBox(maxLength);
        textField.setValue(text);
        textField.setFocused(focused);
        applyNativeState();
    }

    private void syncTextFieldBounds() {
        textField.setX(getAbsoluteX());
        textField.setY(getAbsoluteY());
        textField.setWidth(width);
    }

    private void applyNativeState() {
        textField.setEditable(isEnabled());
        textField.setVisible(isVisible());
        if (!isEnabled() && textField.isFocused()) {
            textField.setFocused(false);
        }
        if (backgroundDrawing != null) {
            textField.setBordered(backgroundDrawing);
        }
    }
    *///?}
}
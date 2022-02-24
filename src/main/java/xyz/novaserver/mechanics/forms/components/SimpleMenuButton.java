package xyz.novaserver.mechanics.forms.components;

import org.geysermc.cumulus.component.ButtonComponent;

public class SimpleMenuButton extends MenuButton {

    private final Runnable buttonHandler;

    public SimpleMenuButton(ButtonComponent buttonComponent, Runnable buttonHandler) {
        super(buttonComponent);
        this.buttonHandler = buttonHandler;
    }

    public Runnable getButtonHandler() {
        return buttonHandler;
    }
}

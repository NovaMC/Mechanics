package xyz.novaserver.mechanics.forms.components;

import org.geysermc.cumulus.component.ButtonComponent;

public class MenuButton {

    private final ButtonComponent buttonComponent;

    public MenuButton(ButtonComponent buttonComponent) {
        this.buttonComponent = buttonComponent;
    }

    public ButtonComponent buttonComponent() {
        return buttonComponent;
    }
}

package xyz.novaserver.mechanics.forms.components;

import org.geysermc.cumulus.component.ButtonComponent;
import xyz.novaserver.mechanics.forms.MenuForm;

public class FormMenuButton extends MenuButton {

    private final MenuForm<?> form;

    public FormMenuButton(ButtonComponent buttonComponent, MenuForm<?> form) {
        super(buttonComponent);
        this.form = form;
    }

    public MenuForm<?> form() {
        return form;
    }
}

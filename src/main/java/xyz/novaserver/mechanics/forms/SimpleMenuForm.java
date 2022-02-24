package xyz.novaserver.mechanics.forms;

import net.kyori.adventure.text.Component;
import org.geysermc.cumulus.Form;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import xyz.novaserver.mechanics.forms.components.FormMenuButton;
import xyz.novaserver.mechanics.forms.components.MenuButton;
import xyz.novaserver.mechanics.forms.components.SimpleMenuButton;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class SimpleMenuForm extends MenuForm<SimpleForm> {

    private final List<MenuButton> menuButtons = new ArrayList<>();
    private String content;

    public SimpleMenuForm(MenuForm<?> parent, Component title, Component content) {
        super(parent, title);
        setContent(content);
    }

    public SimpleMenuForm(MenuForm<?> parent, Component title) {
        this(parent, title, Component.empty());
    }

    public SimpleMenuForm(Component title, Component content) {
        this(null, title, content);
    }

    public SimpleMenuForm(Component title) {
        this(title, Component.empty());
    }

    public String getContent() {
        return content;
    }

    public SimpleMenuForm setContent(Component content) {
        this.content = ItemUtils.toLegacyString(content);
        return this;
    }

    public SimpleMenuForm addFormButton(MenuForm<?> form) {
        menuButtons.add(new FormMenuButton(
                ButtonComponent.of(form.getTitle()), form));
        return this;
    }

    public SimpleMenuForm addSimpleButton(Component text, Runnable buttonHandler) {
        menuButtons.add(new SimpleMenuButton(
                ButtonComponent.of(ItemUtils.toLegacyString(text)), buttonHandler));
        return this;
    }

    @Override
    public SimpleForm create(FloodgatePlayer player) {
        // Create form with title, description, and mapped button list
        SimpleForm form = SimpleForm.of(getTitle(), getContent(), menuButtons.stream().map(MenuButton::getButtonComponent).toList());

        // Handle the response for the buttons on the form
        form.setResponseHandler(data -> {
            SimpleFormResponse response = form.parseResponse(data);
            if (response.isClosed() && getParent() != null) player.sendForm((Form) getParent().create(player));
            if (!response.isCorrect()) return;

            MenuButton button = menuButtons.get(response.getClickedButtonId());
            if (button instanceof SimpleMenuButton) {
                ((SimpleMenuButton) button).getButtonHandler().run();
            } else if (button instanceof FormMenuButton) {
                player.sendForm((Form) ((FormMenuButton) button).getForm().create(player));
            }
        });
        return form;
    }
}

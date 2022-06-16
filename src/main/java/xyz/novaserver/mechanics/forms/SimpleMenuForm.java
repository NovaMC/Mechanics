package xyz.novaserver.mechanics.forms;

import net.kyori.adventure.text.Component;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import xyz.novaserver.mechanics.forms.components.FormMenuButton;
import xyz.novaserver.mechanics.forms.components.MenuButton;
import xyz.novaserver.mechanics.forms.components.SimpleMenuButton;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class SimpleMenuForm extends MenuForm<SimpleForm> {

    private final List<MenuButton> menuButtons = new ArrayList<>();
    private String content;

    public SimpleMenuForm(MenuForm<?> parent, Component title, Component content) {
        super(parent, title);
        content(content);
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

    public String content() {
        return content;
    }

    public SimpleMenuForm content(Component content) {
        this.content = ItemUtils.toLegacyString(content);
        return this;
    }

    public SimpleMenuForm formButton(MenuForm<?> form) {
        menuButtons.add(new FormMenuButton(
                ButtonComponent.of(form.title()), form));
        return this;
    }

    public SimpleMenuForm simpleButton(Component text, Runnable buttonHandler) {
        menuButtons.add(new SimpleMenuButton(
                ButtonComponent.of(ItemUtils.toLegacyString(text)), buttonHandler));
        return this;
    }

    @Override
    public SimpleForm create(FloodgatePlayer player) {
        // Create form with title, description, and mapped button list
        SimpleForm.Builder builder = SimpleForm.builder().title(title()).content(content());
        // Add buttons to builder
        for (MenuButton menuButton : menuButtons) {
            ButtonComponent buttonComponent = menuButton.buttonComponent();
            builder = builder.button(buttonComponent.text(), buttonComponent.image());
        }

        // Set closed handler to send parent form if available
        builder = builder.closedResultHandler(() -> {
            if (parent() != null) {
                player.sendForm((Form) parent().create(player));
            }
        }).validResultHandler(response -> {
            // Handle the response for the buttons on the form
            MenuButton button = menuButtons.get(response.clickedButtonId());
            if (button instanceof SimpleMenuButton) {
                ((SimpleMenuButton) button).buttonHandler().run();
            } else if (button instanceof FormMenuButton) {
                player.sendForm((Form) ((FormMenuButton) button).form().create(player));
            }
        });

        return builder.build();
    }
}

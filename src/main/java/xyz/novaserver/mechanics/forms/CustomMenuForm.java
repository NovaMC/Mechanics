package xyz.novaserver.mechanics.forms;

import net.kyori.adventure.text.Component;
import org.geysermc.cumulus.component.*;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import xyz.novaserver.mechanics.forms.components.CustomMenuComponent;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class CustomMenuForm extends MenuForm<CustomForm> {

    private final List<CustomMenuComponent> components = new ArrayList<>();

    public CustomMenuForm(MenuForm<?> parent, Component title) {
        super(parent, title);
    }

    public CustomMenuForm(Component title) {
        this(null, title);
    }

    public CustomMenuForm dropdown(Consumer<Integer> handler, Component text, int defaultOption, String... options) {
        components.add(new CustomMenuComponent(
                DropdownComponent.of(ItemUtils.toLegacyString(text), Arrays.asList(options), defaultOption), handler));
        return this;
    }

    public CustomMenuForm input(Consumer<String> handler, Component text, String placeholder, String defaultText) {
        components.add(new CustomMenuComponent(
                InputComponent.of(ItemUtils.toLegacyString(text), placeholder, defaultText), handler));
        return this;
    }

    public CustomMenuForm label(Component text) {
        components.add(new CustomMenuComponent(
                LabelComponent.of(ItemUtils.toLegacyString(text)), null));
        return this;
    }

    public CustomMenuForm slider(Consumer<Float> handler, Component text, float min, float max, int step, float defaultValue) {
        components.add(new CustomMenuComponent(
                SliderComponent.of(ItemUtils.toLegacyString(text), min, max, step, defaultValue), handler));
        return this;
    }

    public CustomMenuForm slider(Consumer<Float> handler, Component text, float min, float max) {
        slider(handler, text, min, max, 1, 0);
        return this;
    }

    public CustomMenuForm stepSlider(Consumer<Integer> handler, Component text, int defaultStep, String... steps) {
        components.add(new CustomMenuComponent(
                StepSliderComponent.of(ItemUtils.toLegacyString(text), defaultStep, steps), handler));
        return this;
    }

    public CustomMenuForm toggle(Consumer<Boolean> handler, Component text, boolean defaultValue) {
        components.add(new CustomMenuComponent(
                ToggleComponent.of(ItemUtils.toLegacyString(text), defaultValue), handler));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomForm create(FloodgatePlayer player) {
        // Create a form with title and components
        CustomForm.Builder builder = CustomForm.builder().title(title());
        // Add components to builder
        for (CustomMenuComponent component : components) {
            org.geysermc.cumulus.component.Component cumulusComponent = component.component();
            builder = builder.component(cumulusComponent);
        }

        // Set closed handler to send parent form if available
        builder = builder.closedResultHandler(() -> {
            if (parent() != null) {
                player.sendForm((Form) parent().create(player));
            }
        }).validResultHandler((form, response) -> {
            // Handle the response when the form is submitted
            List<org.geysermc.cumulus.component.Component> content = form.content();
            // Loop through all components and run handler based on correct response type
            for (int i = 0; i < content.size(); i++) {
                Consumer<?> handler = components.get(i).componentHandler();
                org.geysermc.cumulus.component.Component component = content.get(i);
                switch (component.type()) {
                    case DROPDOWN -> ((Consumer<Integer>) handler).accept(response.asDropdown(i));
                    case INPUT -> ((Consumer<String>) handler).accept(response.asInput(i));
                    case SLIDER -> ((Consumer<Float>) handler).accept(response.asSlider(i));
                    case STEP_SLIDER -> ((Consumer<Integer>) handler).accept(response.asStepSlider(i));
                    case TOGGLE -> ((Consumer<Boolean>) handler).accept(response.asToggle(i));
                }
            }
        });

        return builder.build();
    }
}

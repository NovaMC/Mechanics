package xyz.novaserver.mechanics.forms;

import net.kyori.adventure.text.Component;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.Form;
import org.geysermc.cumulus.component.*;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.util.ComponentType;
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

    public CustomMenuForm addDropdown(Consumer<Integer> handler, Component text, int defaultOption, String... options) {
        components.add(new CustomMenuComponent(
                DropdownComponent.of(ItemUtils.toLegacyString(text), Arrays.asList(options), defaultOption), handler));
        return this;
    }

    public CustomMenuForm addInput(Consumer<String> handler, Component text, String placeholder, String defaultText) {
        components.add(new CustomMenuComponent(
                InputComponent.of(ItemUtils.toLegacyString(text), placeholder, defaultText), handler));
        return this;
    }

    public CustomMenuForm addLabel(Component text) {
        components.add(new CustomMenuComponent(
                LabelComponent.of(ItemUtils.toLegacyString(text)), null));
        return this;
    }

    public CustomMenuForm addSlider(Consumer<Float> handler, Component text, float min, float max, int step, float defaultValue) {
        components.add(new CustomMenuComponent(
                SliderComponent.of(ItemUtils.toLegacyString(text), min, max, step, defaultValue), handler));
        return this;
    }

    public CustomMenuForm addSlider(Consumer<Float> handler, Component text, float min, float max) {
        addSlider(handler, text, min, max, 1, 0);
        return this;
    }

    public CustomMenuForm addStepSlider(Consumer<Integer> handler, Component text, int defaultStep, String... steps) {
        components.add(new CustomMenuComponent(
                StepSliderComponent.of(ItemUtils.toLegacyString(text), defaultStep, steps), handler));
        return this;
    }

    public CustomMenuForm addToggle(Consumer<Boolean> handler, Component text, boolean defaultValue) {
        components.add(new CustomMenuComponent(
                ToggleComponent.of(ItemUtils.toLegacyString(text), defaultValue), handler));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomForm create(FloodgatePlayer player) {
        // Create a form with title and components
        CustomForm form = CustomForm.of(getTitle(), null, components.stream().map(CustomMenuComponent::getComponent).toList());

        // Handle the response when the form is submitted
        form.setResponseHandler(data -> {
            CustomFormResponse response = form.parseResponse(data);
            if (response.isClosed() && getParent() != null) player.sendForm((Form) getParent().create(player));
            if (!response.isCorrect()) return;

            // Loop through all components (types) and run handler based on correct response type
            List<ComponentType> typeList = response.getComponentTypes();
            for (int i = 0; i < typeList.size(); i++) {
                Consumer<?> handler = components.get(i).getComponentHandler();
                switch (typeList.get(i)) {
                    case DROPDOWN -> ((Consumer<Integer>) handler).accept(response.getDropdown(i));
                    case INPUT -> ((Consumer<String>) handler).accept(response.getInput(i));
                    case SLIDER -> ((Consumer<Float>) handler).accept(response.getSlider(i));
                    case STEP_SLIDER -> ((Consumer<Integer>) handler).accept(response.getStepSlide(i));
                    case TOGGLE -> ((Consumer<Boolean>) handler).accept(response.getToggle(i));
                }
            }
        });
        return form;
    }
}

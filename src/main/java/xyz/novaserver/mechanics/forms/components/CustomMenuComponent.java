package xyz.novaserver.mechanics.forms.components;

import org.geysermc.cumulus.component.Component;

import java.util.function.Consumer;

public class CustomMenuComponent {

    private final Component component;
    private final Consumer<?> componentHandler;

    public CustomMenuComponent(Component component, Consumer<?> componentHandler) {
        this.component = component;
        this.componentHandler = componentHandler;
    }

    public Component getComponent() {
        return component;
    }

    public Consumer<?> getComponentHandler() {
        return componentHandler;
    }
}

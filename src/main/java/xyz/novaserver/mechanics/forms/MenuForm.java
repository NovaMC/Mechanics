package xyz.novaserver.mechanics.forms;

import net.kyori.adventure.text.Component;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import xyz.novaserver.mechanics.item.ItemUtils;

public abstract class MenuForm<T> {

    private final String title;
    private final MenuForm<?> parent;

    public MenuForm(MenuForm<?> parent, Component title) {
        this.title = ItemUtils.toLegacyString(title);
        this.parent = parent;
    }

    public MenuForm(Component title) {
        this(null, title);
    }

    public String title() {
        return title;
    }

    public MenuForm<?> parent() {
        return parent;
    }

    public abstract T create(FloodgatePlayer player);
}

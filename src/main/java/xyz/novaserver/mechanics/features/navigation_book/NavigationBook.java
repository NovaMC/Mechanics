package xyz.novaserver.mechanics.features.navigation_book;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.mechanics.item.CustomItem;

public class NavigationBook extends CustomItem {
    private static final String id = "navigation_book";
    private static final Material material = Material.BOOK;

    public NavigationBook(Plugin plugin) {
        super(id, material, plugin);
    }

    @Override
    public Component getName() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&dNavigation Book").decoration(TextDecoration.ITALIC, false);
    }
}

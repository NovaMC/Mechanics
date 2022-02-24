package xyz.novaserver.mechanics.features.navigation_book;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.mechanics.item.CustomItem;

public class NavigationBook extends CustomItem {
    private static final String ID = "navigation_book";
    private static final Material MATERIAL = Material.BOOK;
    private static final Component NAME = LegacyComponentSerializer.legacyAmpersand()
            .deserialize("&dNavigation Book").decoration(TextDecoration.ITALIC, false);

    public NavigationBook(Plugin plugin) {
        super(ID, MATERIAL, NAME, plugin);
    }
}

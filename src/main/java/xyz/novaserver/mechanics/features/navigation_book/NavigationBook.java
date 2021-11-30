package xyz.novaserver.mechanics.features.navigation_book;

import org.bukkit.ChatColor;
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
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "Navigation Book";
    }
}

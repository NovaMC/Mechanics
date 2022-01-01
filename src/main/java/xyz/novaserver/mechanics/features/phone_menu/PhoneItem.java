package xyz.novaserver.mechanics.features.phone_menu;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.mechanics.item.CustomItem;
import xyz.novaserver.mechanics.item.ItemUtils;

public class PhoneItem extends CustomItem {
    private static final String id = "phone";
    private static final Material material = Material.PAPER;
    private static final String name = ItemUtils.translateHexColorCodes("&#", "&#55f9d7Phone");

    public PhoneItem(Plugin plugin) {
        super(id, material, plugin);
        ItemMeta meta = getItemMeta();
        meta.setCustomModelData(201);
        setItemMeta(meta);
    }

    @Override
    public String getName() {
        return name;
    }
}

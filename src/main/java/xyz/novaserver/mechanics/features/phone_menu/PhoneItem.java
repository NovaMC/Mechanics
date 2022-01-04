package xyz.novaserver.mechanics.features.phone_menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.mechanics.item.CustomItem;

public class PhoneItem extends CustomItem {
    private static final String id = "phone";
    private static final Material material = Material.PAPER;

    public PhoneItem(Plugin plugin) {
        super(id, material, plugin);
        ItemMeta meta = getItemMeta();
        meta.setCustomModelData(201);
        setItemMeta(meta);
    }

    @Override
    public Component getName() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&#55f9d7Phone");
    }
}

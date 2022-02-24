package xyz.novaserver.mechanics.features.phone_menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.mechanics.item.CustomItem;

public class PhoneItem extends CustomItem {
    private static final String ID = "phone";
    private static final Material MATERIAL = Material.PAPER;
    private static final Component NAME = LegacyComponentSerializer.legacyAmpersand()
            .deserialize("&#55f9d7Phone").decoration(TextDecoration.ITALIC, false);

    public PhoneItem(Plugin plugin) {
        super(ID, MATERIAL, NAME, plugin);
        ItemMeta meta = getItemMeta();
        meta.setCustomModelData(201);
        setItemMeta(meta);
    }
}

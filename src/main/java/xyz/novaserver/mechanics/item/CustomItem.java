package xyz.novaserver.mechanics.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.mechanics.util.ItemUtils;

public abstract class CustomItem extends ItemStack {
    private final Plugin plugin;
    private final String id;

    public CustomItem(String id, Material material, Plugin plugin) {
        super(material, 1);
        this.plugin = plugin;
        this.id = id;

        setName(getName());
        setKeyData(this.id);
    }

    public abstract String getName();

    public String getId() {
        return this.id;
    }

    private void setKeyData(String id) {
        ItemMeta meta = this.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ItemUtils.getIdKey(plugin), PersistentDataType.STRING, id);
        this.setItemMeta(meta);
    }

    private void setName(String name) {
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(name);
        this.setItemMeta(meta);
    }
}

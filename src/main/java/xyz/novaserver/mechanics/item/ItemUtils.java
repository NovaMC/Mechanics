package xyz.novaserver.mechanics.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ItemUtils {
    public static boolean instanceOf(ItemStack itemStack, CustomItem customItem, Plugin plugin) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }
        PersistentDataContainer data = itemStack.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = getIdKey(plugin);

        return data.has(key, PersistentDataType.STRING)
                && data.get(key, PersistentDataType.STRING).equals(customItem.getId());
    }

    public static NamespacedKey getIdKey(Plugin plugin) {
        return new NamespacedKey(plugin, "id");
    }
}

package xyz.novaserver.mechanics.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

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

    public static String translateHexColorCodes(String startTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder builder = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(builder, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(builder).toString();
    }
}

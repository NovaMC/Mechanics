package xyz.novaserver.mechanics.features.phone_menu;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.Collections;
import java.util.List;

public class GivePhoneCommand implements TabExecutor {

    private final PhoneFeature feature;
    private final NovaMechanics plugin;

    public GivePhoneCommand(PhoneFeature feature) {
        this.feature = feature;
        this.plugin = feature.getMechanics();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        if (feature.isFloodgatePlayer(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "The phone can only be used by java players!");
            return true;
        }

        //String message;
        //if (feature.givePlayerPhone(player)) {
        //    message = feature.getMechanics().getConfig().getString("phone.take-out", "");
        //} else {
            player.getInventory().forEach(item -> {
                if (ItemUtils.instanceOf(item, feature.TEST_ITEM, plugin))
                    player.getInventory().remove(item);
            });
            player.getPersistentDataContainer().set(feature.PHONE_KEY, PersistentDataType.BYTE, (byte) 1);
            //message = plugin.getConfig().getString("phone.put-away", "");
        //}

        String cmd = plugin.getConfig().getString("phone.command", "").replaceAll("%player%", player.getName());
        // Decide if we should run command on the player or console
        if (plugin.getConfig().getBoolean("phone.run-as-player", true)) {
            player.performCommand(cmd);
        } else {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
        }

        //sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}

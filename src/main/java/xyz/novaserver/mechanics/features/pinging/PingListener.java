package xyz.novaserver.mechanics.features.pinging;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

public class PingListener implements Listener {

    private final JavaPlugin plugin;

    public PingListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("enable-pinging")) {
            return;
        }

        String message = event.getMessage();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StringUtils.containsIgnoreCase(event.getMessage(), player.getName())) {
                playPing(player);
                message = message.replaceAll("(?i)(@?)" + Pattern.quote(player.getName()),
                        ChatColor.RESET.toString() + ChatColor.AQUA + "@" + player.getName() + ChatColor.RESET);
            }
        }

        event.setMessage(message);
    }

    private void playPing(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0F, 0.630F);
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1.0F, 0.845F);
            }, 3L);
        });
    }
}

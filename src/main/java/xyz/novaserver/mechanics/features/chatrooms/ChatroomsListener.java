package xyz.novaserver.mechanics.features.chatrooms;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionEnterEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionExitEvent;
import xyz.novaserver.placeholders.paper.Main;

import java.util.*;

public class ChatroomsListener implements Listener {
    private final ChatroomsFeature feature;
    private final ChatroomFormatter formatter;
    private final Main placeholders;

    private final Map<UUID, Chatroom> playerMap = new HashMap<>();

    public ChatroomsListener(ChatroomsFeature feature) {
        // feature
        this.feature = feature;
        this.formatter = new ChatroomFormatter(feature);
        this.placeholders = (Main) Bukkit.getPluginManager().getPlugin("NovaPlaceholders");
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        UUID player = event.getPlayer().getUniqueId();

        if (playerMap.containsKey(player)) {
            Chatroom playerChatroom = playerMap.get(player);
            Set<Audience> viewers = new HashSet<>(event.viewers());
            viewers.forEach(viewer -> {
                if (viewer instanceof Player p) {
                    if (!playerMap.containsKey(p.getUniqueId())
                            || !playerMap.get(p.getUniqueId()).getId().equals(playerChatroom.getId())) {
                        event.viewers().remove(p);
                    }
                }
            });
        }
        else {
            playerMap.keySet().forEach(p -> event.viewers().remove(Bukkit.getPlayer(p)));
        }
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.getChatRoomFlag());
        UUID uuid = event.getPlayer().getUniqueId();

        // Player has entered or changed their chatroom
        if (!playerMap.containsKey(uuid) || playerMap.get(uuid) == null || !playerMap.get(uuid).getId().equals(chatroom)) {
            if (feature.getChatroomMap().containsKey(chatroom)) {
                playerMap.put(uuid, feature.getChatroomMap().get(chatroom));

                // Set chatroom formatter on player
                placeholders.getChatManager().getFancyRenderer().setFormat(uuid, formatter);
            }
        }
    }

    @EventHandler
    public void onRegionExit(RegionExitEvent event) {
        String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.getChatRoomFlag());
        UUID uuid = event.getPlayer().getUniqueId();

        // Player has exited a chatroom
        if (chatroom == null || chatroom.equals("undefined") || !feature.getChatroomMap().containsKey(chatroom)) {
            playerMap.remove(uuid);

            // Remove chatroom formatter on player
            placeholders.getChatManager().getFancyRenderer().removeFormat(uuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerMap.remove(event.getPlayer().getUniqueId());
    }
}

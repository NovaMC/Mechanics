package xyz.novaserver.mechanics.features.chatrooms.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.novaserver.core.event.RegionEnterEvent;
import xyz.novaserver.core.event.RegionExitEvent;
import xyz.novaserver.core.event.RegionInitializeEvent;
import xyz.novaserver.mechanics.features.chatrooms.Chatroom;
import xyz.novaserver.mechanics.features.chatrooms.ChatroomFormatter;
import xyz.novaserver.mechanics.features.chatrooms.ChatroomsFeature;
import xyz.novaserver.mechanics.features.chatrooms.util.TitleData;
import xyz.novaserver.placeholders.paper.Main;

import java.util.*;

public class ChatroomsListener implements Listener {
    private final ChatroomsFeature feature;

    private final ChatroomFormatter formatter;
    private final TitleData titleData;
    private final Main placeholders;

    private final Map<UUID, Chatroom> playerMap;

    public ChatroomsListener(ChatroomsFeature feature) {
        // feature
        this.feature = feature;
        formatter = new ChatroomFormatter(feature);
        placeholders = (Main) Bukkit.getPluginManager().getPlugin("NovaPlaceholders");
        titleData = feature.titleData();
        playerMap = feature.playerMap();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        final UUID player = event.getPlayer().getUniqueId();

        // If the message sender is in a chatroom
        // Blocks chatroom messages from being sent to global chat
        if (playerMap.containsKey(player)) {
            // Grab their chatroom
            final Chatroom playerChatroom = playerMap.get(player);
            // Clone the viewer set from the event
            Set<Audience> viewers = new HashSet<>(event.viewers());

            // Remove all viewers that are not in a chatroom, or not in the same chatroom as the sender
            viewers.forEach(viewer -> {
                if (viewer instanceof Player p) {
                    if (!playerMap.containsKey(p.getUniqueId())
                            || !playerMap.get(p.getUniqueId()).getId().equals(playerChatroom.getId())) {
                        event.viewers().remove(p);
                    }
                }
            });
        } else {
            // If message sender is not in a chatroom remove everyone in a chatroom from the viewers set
            // Blocks global message from being sent to chatroom users
            playerMap.keySet().forEach(p -> event.viewers().remove(Bukkit.getPlayer(p)));
        }
    }

    @EventHandler
    public void onRegionInitialize(RegionInitializeEvent event) {
        final String chatroom = event.getApplicableSet().queryValue(event.getPlayer(), feature.chatroomFlag());
        final UUID uuid = event.getPlayer().getUniqueId();

        onRegionJoin(uuid, chatroom);
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        final String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.chatroomFlag());
        final UUID uuid = event.getPlayer().getUniqueId();

        onRegionJoin(uuid, chatroom);
    }

    @EventHandler
    public void onRegionExit(RegionExitEvent event) {
        final String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.chatroomFlag());
        final UUID uuid = event.getPlayer().getUniqueId();

        // Player has exited a chatroom
        if (playerMap.containsKey(uuid) && (chatroom == null || !feature.chatrooms().containsKey(chatroom))) {
            // Remove chatroom formatter on player
            placeholders.getChatManager().getFancyRenderer().removeFormat(uuid);
            // Send the leave title and sound to the player
            titleData.sendLeave(Bukkit.getPlayer(uuid), playerMap.get(uuid));
            // Remove player from the chatroom
            playerMap.remove(uuid);
        }
    }

    private void onRegionJoin(UUID uuid, String chatroom) {
        // Player has entered or changed their chatroom
        if (!playerMap.containsKey(uuid) || playerMap.get(uuid) == null
                || !playerMap.get(uuid).getId().equals(chatroom)) {

            // Return if the specified chatroom does not exist
            if (!feature.chatrooms().containsKey(chatroom)) {
                return;
            }
            // Add player to chatroom
            playerMap.put(uuid, feature.chatrooms().get(chatroom));
            // Set chatroom formatter on player
            placeholders.getChatManager().getFancyRenderer().setFormat(uuid, formatter);
            // Send the join title and play sound
            titleData.sendJoin(Bukkit.getPlayer(uuid), playerMap.get(uuid));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerMap.remove(event.getPlayer().getUniqueId());
    }
}

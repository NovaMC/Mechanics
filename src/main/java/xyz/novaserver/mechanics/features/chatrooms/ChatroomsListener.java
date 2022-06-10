package xyz.novaserver.mechanics.features.chatrooms;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.novaserver.mechanics.features.chatrooms.util.TitleData;
import xyz.novaserver.mechanics.features.wg_events.event.RegionEnterEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionExitEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionInitializeEvent;
import xyz.novaserver.placeholders.paper.Main;

import java.util.*;

public class ChatroomsListener implements Listener {
    private final ChatroomsFeature feature;

    private final ChatroomFormatter formatter;
    private final Main placeholders;

    private final TitleData titleData;
    private final Map<UUID, Chatroom> playerMap = new HashMap<>();

    public ChatroomsListener(ChatroomsFeature feature) {
        // feature
        this.feature = feature;
        this.formatter = new ChatroomFormatter(feature);
        this.placeholders = (Main) Bukkit.getPluginManager().getPlugin("NovaPlaceholders");
        this.titleData = feature.getTitleData();
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
        } else {
            playerMap.keySet().forEach(p -> event.viewers().remove(Bukkit.getPlayer(p)));
        }
    }

    @EventHandler
    public void onDiscordChat(DiscordChatMessageEvent event) {
        // Block EssentialsDiscord messages from being sent when player is in a chatroom
        if (playerMap.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRegionInitialize(RegionInitializeEvent event) {
        final String chatroom = event.getApplicableSet().queryValue(event.getPlayer(), feature.getChatRoomFlag());
        final UUID uuid = event.getPlayer().getUniqueId();

        onRegionJoin(uuid, chatroom);
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        final String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.getChatRoomFlag());
        final UUID uuid = event.getPlayer().getUniqueId();

        onRegionJoin(uuid, chatroom);
    }

    @EventHandler
    public void onRegionExit(RegionExitEvent event) {
        final String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.getChatRoomFlag());
        final UUID uuid = event.getPlayer().getUniqueId();

        // Player has exited a chatroom
        if (playerMap.containsKey(uuid) && (chatroom == null || !feature.getChatroomMap().containsKey(chatroom))) {
            // Remove chatroom formatter on player
            placeholders.getChatManager().getFancyRenderer().removeFormat(uuid);

            Player player = Bukkit.getPlayer(uuid);
            titleData.sendLeave(player, playerMap.get(uuid));

            playerMap.remove(uuid);
        }
    }

    private void onRegionJoin(UUID uuid, String chatroom) {
        // Player has entered or changed their chatroom
        if (!playerMap.containsKey(uuid) || playerMap.get(uuid) == null || !playerMap.get(uuid).getId().equals(chatroom)) {
            if (feature.getChatroomMap().containsKey(chatroom)) {
                playerMap.put(uuid, feature.getChatroomMap().get(chatroom));
                // Set chatroom formatter on player
                placeholders.getChatManager().getFancyRenderer().setFormat(uuid, formatter);

                Player player = Bukkit.getPlayer(uuid);
                titleData.sendJoin(player, playerMap.get(uuid));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerMap.remove(event.getPlayer().getUniqueId());
    }
}

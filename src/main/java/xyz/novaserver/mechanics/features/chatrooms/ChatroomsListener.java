package xyz.novaserver.mechanics.features.chatrooms;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.mechanics.features.wg_events.event.RegionEnterEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionExitEvent;
import xyz.novaserver.placeholders.paper.Main;
import xyz.novaserver.placeholders.paper.util.MetaUtils;

import java.util.*;

public class ChatroomsListener implements Listener {
    private final ChatroomsFeature feature;
    private final ChatroomFormatter formatter;
    private final Main placeholders;
    private final TitleData config;
    private final Map<UUID, Chatroom> playerMap = new HashMap<>();

    public ChatroomsListener(ChatroomsFeature feature) {
        // feature
        this.feature = feature;
        this.formatter = new ChatroomFormatter(feature);
        this.placeholders = (Main) Bukkit.getPluginManager().getPlugin("NovaPlaceholders");
        this.config = feature.getTitleData();
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

    @SuppressWarnings("PatternValidation")
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

                Player p = Bukkit.getPlayer(uuid);
                if(p == null) return;
                // play sound
                p.playSound(Sound.sound(Key.key(config.getJoinSound()), Sound.Source.valueOf(config.getSoundCategory()), config.getVolume(), config.getPitch()));
                // check for bedrock
                boolean isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId());
                if(!isBedrock) {
                    p.showTitle(Title.title(MetaUtils.asComponent(config.getJoinMessage()), MetaUtils.asComponent(config.getJoinSubMessage())));
                }
                else {
                    p.showTitle(Title.title(MetaUtils.asComponent(config.getJoinMessageBedrock()), MetaUtils.asComponent(config.getJoinSubMessageBedrock())));
                }
            }
        }
    }

    @SuppressWarnings("PatternValidation")
    @EventHandler
    public void onRegionExit(RegionExitEvent event) {
        String chatroom = event.getToSet().queryValue(event.getPlayer(), feature.getChatRoomFlag());
        UUID uuid = event.getPlayer().getUniqueId();

        // Player has exited a chatroom
        if (chatroom == null || chatroom.equals("undefined") || !feature.getChatroomMap().containsKey(chatroom)) {
            playerMap.remove(uuid);

            // Remove chatroom formatter on player
            placeholders.getChatManager().getFancyRenderer().removeFormat(uuid);

            Player p = Bukkit.getPlayer(uuid);
            if(p == null) return;
            // play sound
            p.playSound(Sound.sound(Key.key(config.getLeaveSound()), Sound.Source.valueOf(config.getSoundCategory()), config.getVolume(), config.getPitch()));
            // check for bedrock
            boolean isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId());
            if(!isBedrock) {
                p.showTitle(Title.title(MetaUtils.asComponent(config.getLeaveMessage()), MetaUtils.asComponent(config.getLeaveSubMessage())));
            }
            else {
                p.showTitle(Title.title(MetaUtils.asComponent(config.getLeaveMessageBedrock()), MetaUtils.asComponent(config.getLeaveSubMessageBedrock())));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerMap.remove(event.getPlayer().getUniqueId());
    }
}

package xyz.novaserver.mechanics.features.chatrooms;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ChatroomsListener implements Listener {
    private final ChatroomsFeature feature;
    private final WorldGuard worldGuard = WorldGuard.getInstance();
    private final Map<UUID, Chatroom> playerMap = new HashMap<>();

    public ChatroomsListener(ChatroomsFeature feature) {
        // feature
        this.feature = feature;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        UUID player = event.getPlayer().getUniqueId();

        if(playerMap.containsKey(player)) {
            Chatroom playerChatroom = playerMap.get(player);
            Set<Audience> viewers = new HashSet<>(event.viewers());
            for (Audience a : viewers) {
                if (a instanceof Player p) {
                    if(!playerMap.containsKey(p.getUniqueId())
                            || !playerMap.get(p.getUniqueId()).getId().equals(playerChatroom.getId())) {
                        event.viewers().remove(p);
                    }
                }
            }
        }
        else {
            playerMap.keySet().forEach(p -> {
                event.viewers().remove(Bukkit.getPlayer(p));
            });
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // grab player's world guard region
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        ApplicableRegionSet regionSet = container.createQuery().getApplicableRegions(player.getLocation());
        String chatroom = regionSet.queryValue(player, feature.getChatRoomFlag());
        UUID uuid = player.getUniqueId();
        if(chatroom == null || chatroom.equals("undefined") || !feature.getChatroomMap().containsKey(chatroom)) {
            playerMap.remove(uuid);
        }
        else if (!playerMap.containsKey(uuid) || !playerMap.get(uuid).getId().equals(chatroom)) {
            playerMap.put(uuid, feature.getChatroomMap().get(chatroom));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerMap.remove(event.getPlayer().getUniqueId());
    }
}

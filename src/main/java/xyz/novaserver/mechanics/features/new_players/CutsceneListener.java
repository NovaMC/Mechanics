package xyz.novaserver.mechanics.features.new_players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CutsceneListener implements Listener {
    private final NewPlayersFeature feature;
    private final Set<UUID> firstJoinSet = new HashSet<>();

    public CutsceneListener(NewPlayersFeature feature) {
        this.feature = feature;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (firstJoinSet.contains(player.getUniqueId())) {
            firstJoinSet.remove(player.getUniqueId());
            feature.playCutscene(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().runTaskLater(feature.getMechanics(), () -> {
                firstJoinSet.add(player.getUniqueId());
            }, feature.getConfig().getLong("new-players.cutscene.delay"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        firstJoinSet.remove(event.getPlayer().getUniqueId());
    }
}

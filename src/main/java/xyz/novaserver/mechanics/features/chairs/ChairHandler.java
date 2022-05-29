package xyz.novaserver.mechanics.features.chairs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.mechanics.features.chairs.event.ChairSitEvent;
import xyz.novaserver.mechanics.features.chairs.event.ChairUnsitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChairHandler {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Chair> sitting = new HashMap<>();

    public ChairHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void sit(Player player, Block block) {
        if (sitting.containsKey(player.getUniqueId())) {
            if (sitting.get(player.getUniqueId()).getChair().equals(block)) {
                return;
            }
            dismount(player);
        }

        Chair chair = new Chair(plugin, player, block);
        ChairSitEvent event = ChairSitEvent.callEvent(chair);
        if (!event.isCancelled()) {
            sitting.put(player.getUniqueId(), chair);
        } else {
            chair.dismount();
        }
    }

    public void dismount(Player player) {
        Chair chair = sitting.get(player.getUniqueId());
        ChairUnsitEvent event = ChairUnsitEvent.callEvent(chair);
        if (!event.isCancelled()) {
            chair.dismount();
            sitting.remove(player.getUniqueId());
        }
    }

    public void unsit(Player player) {
        Chair chair = sitting.get(player.getUniqueId());
        ChairUnsitEvent event = ChairUnsitEvent.callEvent(chair);
        if (!event.isCancelled()) {
            chair.dismount();
            chair.teleport();
            sitting.remove(player.getUniqueId());
        }
    }

    public void unsit(Block block) {
        UUID uuid = null;
        Chair chair = null;

        for (Chair c : sitting.values()) {
            if (c.getChair().equals(block)) {
                uuid = c.getPlayer().getUniqueId();
                chair = c;
            }
        }

        if (uuid != null) {
            ChairUnsitEvent event = ChairUnsitEvent.callEvent(chair);
            if (!event.isCancelled()) {
                chair.dismount();
                chair.teleport();
                sitting.remove(uuid);
            }
        }
    }

    public boolean isOccupied(Block block) {
        for (Chair data : sitting.values()) {
            if (data.getChair().equals(block))
                return true;
        }
        return false;
    }

    public boolean isSitting(Player player) {
        return sitting.containsKey(player.getUniqueId());
    }
}

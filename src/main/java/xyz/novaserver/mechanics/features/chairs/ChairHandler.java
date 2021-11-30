package xyz.novaserver.mechanics.features.chairs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
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
        sitting.put(player.getUniqueId(), new Chair(plugin, player, block));
    }

    public void dismount(Player player) {
        sitting.get(player.getUniqueId()).dismount();
        sitting.remove(player.getUniqueId());
    }

    public void unsit(Player player) {
        sitting.get(player.getUniqueId()).dismount();
        sitting.get(player.getUniqueId()).teleport();
        sitting.remove(player.getUniqueId());
    }

    public void unsit(Block chair) {
        UUID remove = null;
        Iterator<UUID> iterator = sitting.keySet().iterator();
        if (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Chair data = sitting.get(uuid);

            if (data.getChair().equals(chair)) {
                data.dismount();
            }

            remove = uuid;
        }

        if (remove != null) {
            sitting.remove(remove);
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

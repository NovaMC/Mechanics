package xyz.novaserver.mechanics.features.chairs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

public class Chair {
    private final JavaPlugin plugin;

    private final Player player;
    private Block chair;
    private ArmorStand stand;

    private BukkitTask task;

    public Chair(JavaPlugin plugin, Player player, Block chair) {
        this.plugin = plugin;
        this.player = player;
        this.chair = chair;

        createChair();
    }

    public void createChair() {
        Location loc = chair.getLocation().add(0.5D, 0.3D, 0.5D);

        // Remove existing stands if they exist
        /* Temporary cleanup code
        Collection<ArmorStand> near = loc.getNearbyEntitiesByType(ArmorStand.class, 0.5D);
        near.stream()
                .filter(e -> !e.isVisible() && e.isInvulnerable())
                .forEach(Entity::remove);
        */
        this.stand = loc.getWorld()
                .spawn(loc, ArmorStand.class, settings -> {
            settings.setGravity(false);
            settings.setMarker(true);
            settings.setSmall(true);
            settings.setVisible(false);
            settings.setCollidable(false);
            settings.setInvulnerable(true);
            settings.addPassenger(player);
        });

        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                () -> stand.setRotation(player.getLocation().getYaw(), 0.0f), 4L, 4L);
    }

    public Block getChair() {
        return chair;
    }

    public void dismount() {
        task.cancel();
        this.player.leaveVehicle();
        this.stand.remove();
    }

    public void teleport() {
        final List<BlockFace> faces = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST);
        boolean validSpot = false;
        Location teleportTo = player.getLocation();

        for (BlockFace face : faces) {
            Block relative = chair.getRelative(face);

            if (relative.isEmpty() && relative.getRelative(BlockFace.UP).isEmpty() && relative.getRelative(BlockFace.DOWN).isSolid()) {
                Location blockLoc = relative.getLocation().add(0.5D, 0.0D, 0.5D);
                teleportTo.set(blockLoc.getX(), blockLoc.getY(), blockLoc.getZ());
                validSpot = true;
                break;
            }
        }

        if (!validSpot) {
            Block relative = chair.getRelative(BlockFace.UP);
            Location blockLoc = relative.getLocation().add(0.5D, 0.0D, 0.5D);
            teleportTo.set(blockLoc.getX(), blockLoc.getY(), blockLoc.getZ());
        }

        player.teleportAsync(teleportTo);
    }
}

package xyz.novaserver.mechanics.features.launch_pads;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class LaunchpadListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null && block.getType().toString().contains("PRESSURE_PLATE")) {
            if (block.getRelative(BlockFace.DOWN, 2).getType() == Material.REDSTONE_BLOCK) {
                // Set player velocity based on their look direction
                Vector playerLook = player.getLocation().getDirection();
                Vector launchVec = new Vector(playerLook.getX() * 3.0D, 1.075D, playerLook.getZ() * 3.0D);
                player.setVelocity(launchVec);
            }
        }
    }
}

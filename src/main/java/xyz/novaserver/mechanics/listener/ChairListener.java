package xyz.novaserver.mechanics.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spigotmc.event.entity.EntityDismountEvent;
import xyz.novaserver.mechanics.chair.ChairHandler;
import xyz.novaserver.mechanics.chair.ChairInitializer;

public class ChairListener implements Listener {

    private final ChairHandler handler;

    public ChairListener(ChairInitializer initializer) {
        this.handler = initializer.getHandler();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND && !player.isSneaking()
                && player.getInventory().getItemInMainHand().getType() == Material.AIR) {

            Block block = e.getClickedBlock();
            BlockData data = block.getBlockData();
            boolean bottomHalf = false;

            if ((e.getClickedBlock().getType().name().contains("STAIRS")
                    || e.getClickedBlock().getType().name().contains("SLAB"))) {

                if (data instanceof Stairs stairs) {
                    bottomHalf = stairs.getHalf() == Bisected.Half.BOTTOM;
                }
                else if (data instanceof Slab slab) {
                    bottomHalf = slab.getType() == Slab.Type.BOTTOM;
                }

                if (player.getEyeLocation().distanceSquared(block.getLocation().add(0.5D, 0.5D, 0.5D)) > 5.0D) {
                    return;
                }

                Block relative = block.getRelative(BlockFace.UP);
                if (bottomHalf && relative.isPassable() && !relative.isLiquid()
                        && player.getLocation().getY() + 1.0D >= block.getY() ) {

                    e.setCancelled(true);
                    if (!handler.isOccupied(e.getClickedBlock())) {
                        handler.sit(player, e.getClickedBlock());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player)e.getEntity();

            if (handler.isSitting(player)) {
                if (player.isSneaking()) {
                    handler.unsit(player);
                }
                else {
                    handler.dismount(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (handler.isSitting(player)) {
            handler.dismount(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (handler.isSitting(player)) {
            handler.dismount(player);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (handler.isSitting(player)) {
            handler.dismount(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (handler.isOccupied(e.getBlock())){
            handler.unsit(e.getBlock());
        }
    }
}

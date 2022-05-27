package xyz.novaserver.mechanics.features.void_fall;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.novaserver.mechanics.NovaMechanics;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoidFallListener implements Listener {

    private final NovaMechanics plugin;
    public final Map<UUID, Boolean> voidFallMap = new HashMap<>();

    private double triggerAtY;
    private double fallHeight;
    private boolean preventDeath;
    private boolean customDamage;
    private double damageOnFall;
    private String worldName;
    private String endName;

    public VoidFallListener(NovaMechanics plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfig();

        triggerAtY = config.getDouble("fallfromvoid.trigger-at-y");
        fallHeight = config.getDouble("fallfromvoid.fall-height");
        preventDeath = config.getBoolean("fallfromvoid.prevent-death");
        customDamage = config.getBoolean("fallfromvoid.custom-damage");
        damageOnFall = config.getDouble("fallfromvoid.damage-on-fall");
        worldName = config.getString("fallfromvoid.world");
        endName = config.getString("fallfromvoid.end-world");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        voidFallMap.put(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        voidFallMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        voidFallMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        boolean inWorld = player.getWorld().getName().equals(worldName);
        boolean inEnd = player.getWorld().getName().equals(endName);

        if (inWorld || inEnd) {
            boolean inVoid = event.getTo().getY() <= triggerAtY;
            boolean aboveSky = event.getTo().getY() >= fallHeight;
            boolean inSky = event.getTo().getY() >= fallHeight - (fallHeight / 4.0);

            // Clear passengers & set as falling
            if ((inEnd && inVoid) || (inWorld && aboveSky)) {
                // Dismount passengers and mounts
                if (!player.getPassengers().isEmpty()) {
                    player.getPassengers().clear();
                }
                player.leaveVehicle();

                // Set player as falling
                voidFallMap.put(player.getUniqueId(), true);
            }

            // Teleport players and add blindness
            if (inEnd && inVoid) {
                // Set location and teleport player to overworld
                Location teleportTo = player.getLocation();
                teleportTo.setWorld(Bukkit.getWorld(worldName));
                teleportTo.setY(fallHeight - 20);
                player.teleport(teleportTo);
            }
            else if (inWorld && inSky) {
                // Add blindness to mask the teleport
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            }
            else if (inWorld && aboveSky) {
                // Set location and teleport player to the end
                Location teleportTo = player.getLocation();
                teleportTo.setWorld(Bukkit.getWorld(endName));
                teleportTo.setY(fallHeight + 20);
                player.teleport(teleportTo);
            }

            // Remove falling if player is safe from damage
            if (voidFallMap.get(player.getUniqueId())) {
                Material m = player.getLocation().getBlock().getType();
                boolean isSafe = player.isFlying() || player.getAllowFlight() || player.isGliding() || player.getLocation().getBlock().isLiquid()
                        || m == Material.COBWEB || m == Material.LADDER || m == Material.VINE;

                if (isSafe) {
                    voidFallMap.put(player.getUniqueId(), false);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if (voidFallMap.get(player.getUniqueId())) {
            if (!event.isCancelled()) {
                double damage = 0.0D;

                if (customDamage) {
                    damage = damageOnFall;
                    if (preventDeath && player.getHealth() - damage <= 0.0D) {
                        damage = player.getHealth() - 1.0D;
                    }
                }
                else if (preventDeath) {
                    damage = player.getHealth() - 1.0D;
                }

                if (damage > 0.0D) {
                    event.setDamage(damage);
                    double damageMultiplier = event.getDamage() / event.getFinalDamage();
                    event.setDamage(damage * damageMultiplier);
                }
            }

            voidFallMap.put(player.getUniqueId(), false);
        }
    }
}

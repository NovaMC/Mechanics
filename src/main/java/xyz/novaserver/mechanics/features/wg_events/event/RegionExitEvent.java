package xyz.novaserver.mechanics.features.wg_events.event;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RegionExitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final LocalPlayer player;
    private final ApplicableRegionSet toSet;
    private final Set<ProtectedRegion> exited;
    private boolean cancelled = false;

    public RegionExitEvent(LocalPlayer player, ApplicableRegionSet toSet, Set<ProtectedRegion> exited) {
        this.player = player;
        this.toSet = toSet;
        this.exited = exited;
    }

    public LocalPlayer getPlayer() {
        return player;
    }

    public ApplicableRegionSet getToSet() {
        return toSet;
    }

    public Set<ProtectedRegion> getExited() {
        return exited;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static RegionExitEvent callEvent(LocalPlayer player, ApplicableRegionSet toSet, Set<ProtectedRegion> exited) {
        RegionExitEvent event = new RegionExitEvent(player, toSet, exited);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}

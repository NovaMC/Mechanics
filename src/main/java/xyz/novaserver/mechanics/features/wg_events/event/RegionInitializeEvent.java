package xyz.novaserver.mechanics.features.wg_events.event;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RegionInitializeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final LocalPlayer player;
    private final ApplicableRegionSet applicableSet;
    private final Set<ProtectedRegion> set;

    public RegionInitializeEvent(LocalPlayer player, ApplicableRegionSet applicableSet, Set<ProtectedRegion> set) {
        this.player = player;
        this.applicableSet = applicableSet;
        this.set = set;
    }

    public LocalPlayer getPlayer() {
        return player;
    }

    public ApplicableRegionSet getApplicableSet() {
        return applicableSet;
    }

    public Set<ProtectedRegion> getSet() {
        return set;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static RegionInitializeEvent callEvent(LocalPlayer player, ApplicableRegionSet applicableSet, Set<ProtectedRegion> set) {
        RegionInitializeEvent event = new RegionInitializeEvent(player, applicableSet, set);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }
}

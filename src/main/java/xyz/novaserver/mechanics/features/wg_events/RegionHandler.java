package xyz.novaserver.mechanics.features.wg_events;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import xyz.novaserver.mechanics.features.wg_events.event.RegionEnterEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionExitEvent;
import xyz.novaserver.mechanics.features.wg_events.event.RegionInitializeEvent;

import java.util.Set;

public class RegionHandler extends Handler {

    public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<RegionHandler> {
        @Override
        public RegionHandler create(Session session) {
            return new RegionHandler(session);
        }
    }

    public RegionHandler(Session session) {
        super(session);
    }

    @Override
    public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
        if (!set.getRegions().isEmpty()) {
            RegionInitializeEvent.callEvent(player, set, set.getRegions());
        }
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (!entered.isEmpty()) {
            RegionEnterEvent enterEvent = RegionEnterEvent.callEvent(player, toSet, entered);
            if (moveType.isCancellable() && enterEvent.isCancelled()) return false;
        }

        if (!exited.isEmpty()) {
            RegionExitEvent exitEvent = RegionExitEvent.callEvent(player, toSet, exited);
            if (moveType.isCancellable() && exitEvent.isCancelled()) return false;
        }

        return true;
    }
}

package xyz.novaserver.mechanics.features.wg_events;

import com.sk89q.worldguard.WorldGuard;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class WGEventsFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        if (mechanics.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(RegionHandler.FACTORY, null);
        }
    }
}

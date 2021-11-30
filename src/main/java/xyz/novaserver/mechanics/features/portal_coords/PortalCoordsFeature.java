package xyz.novaserver.mechanics.features.portal_coords;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class PortalCoordsFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        mechanics.getCommand("portalcoords").setExecutor(new PortalCoordsCommand(mechanics));
    }
}

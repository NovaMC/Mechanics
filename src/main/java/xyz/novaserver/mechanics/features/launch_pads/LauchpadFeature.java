package xyz.novaserver.mechanics.features.launch_pads;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class LauchpadFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        mechanics.getServer().getPluginManager().registerEvents(new LaunchpadListener(), mechanics);
    }
}

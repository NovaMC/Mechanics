package xyz.novaserver.mechanics.features.pinging;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class PingingFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        mechanics.getServer().getPluginManager().registerEvents(new PingListener(mechanics), mechanics);
    }
}

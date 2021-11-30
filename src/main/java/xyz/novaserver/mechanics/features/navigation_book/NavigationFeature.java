package xyz.novaserver.mechanics.features.navigation_book;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class NavigationFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        if (mechanics.getServer().getPluginManager().isPluginEnabled("floodgate")
                && mechanics.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            mechanics.getServer().getPluginManager().registerEvents(new NavigationListener(mechanics), mechanics);
        }
    }
}

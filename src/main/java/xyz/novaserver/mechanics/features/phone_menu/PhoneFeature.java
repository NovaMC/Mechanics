package xyz.novaserver.mechanics.features.phone_menu;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

public class PhoneFeature implements Feature {
    @Override
    public void register(NovaMechanics mechanics) {
        mechanics.getServer().getPluginManager().registerEvents(new PhoneListener(mechanics), mechanics);
    }
}

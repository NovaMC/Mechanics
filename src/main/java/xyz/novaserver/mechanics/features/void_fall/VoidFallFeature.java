package xyz.novaserver.mechanics.features.void_fall;

import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;

public class VoidFallFeature implements Feature, Reloadable {

    private VoidFallListener voidFallListener;

    @Override
    public void register(NovaMechanics mechanics) {
        voidFallListener = new VoidFallListener(mechanics);
        mechanics.getServer().getPluginManager().registerEvents(voidFallListener, mechanics);
    }


    @Override
    public void reload(NovaMechanics mechanics) {
        if (voidFallListener != null) {
            voidFallListener.reload();
        }
    }
}

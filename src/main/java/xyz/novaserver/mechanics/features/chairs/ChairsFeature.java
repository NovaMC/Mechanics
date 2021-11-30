package xyz.novaserver.mechanics.features.chairs;

import org.bukkit.entity.Player;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Cleanable;
import xyz.novaserver.mechanics.features.Feature;

public class ChairsFeature implements Feature, Cleanable {

    private ChairHandler handler;

    @Override
    public void register(NovaMechanics mechanics) {
        this.handler = new ChairHandler(mechanics);
        mechanics.getServer().getPluginManager().registerEvents(new ChairListener(this), mechanics);
    }

    @Override
    public void clean(NovaMechanics mechanics) {
        for (Player player : mechanics.getServer().getOnlinePlayers()) {
            if (handler.isSitting(player)) {
                handler.unsit(player);
            }
        }
    }

    protected ChairHandler getHandler() {
        return handler;
    }
}

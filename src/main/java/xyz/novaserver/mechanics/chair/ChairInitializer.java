package xyz.novaserver.mechanics.chair;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.mechanics.listener.ChairListener;

public class ChairInitializer {
    private ChairHandler handler;
    private JavaPlugin plugin;

    public void initialize(JavaPlugin plugin) {
        this.plugin = plugin;
        this.handler = new ChairHandler(this);
        plugin.getServer().getPluginManager().registerEvents(new ChairListener(this), plugin);
    }

    public ChairHandler getHandler() {
        return handler;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}

package xyz.novaserver.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.mechanics.chair.ChairInitializer;
import xyz.novaserver.mechanics.command.MechanicsCommand;
import xyz.novaserver.mechanics.command.PortalCoordsCommand;
import xyz.novaserver.mechanics.listener.NavigationListener;
import xyz.novaserver.mechanics.listener.PingListener;
import xyz.novaserver.mechanics.listener.VoidFallListener;

public class NovaMechanics extends JavaPlugin {

    private VoidFallListener voidFallListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("enable-chairs")) {
            new ChairInitializer().initialize(this);
        }
        if (getConfig().getBoolean("enable-ffv")) {
            voidFallListener = new VoidFallListener(this);
            getServer().getPluginManager().registerEvents(voidFallListener, this);
        }
        if (getConfig().getBoolean("enable-portalcoords")) {
            getCommand("portalcoords").setExecutor(new PortalCoordsCommand(this));
        }
        if (getConfig().getBoolean("enable-pinging")) {
            getServer().getPluginManager().registerEvents(new PingListener(this), this);
        }
        if (getConfig().getBoolean("enable-navigation-book")
                && getServer().getPluginManager().isPluginEnabled("floodgate")
                && getServer().getPluginManager().isPluginEnabled("Essentials")) {
            getServer().getPluginManager().registerEvents(new NavigationListener(this), this);
        }

        getCommand("novamech").setExecutor(new MechanicsCommand(this));
    }

    public void reload() {
        reloadConfig();

        if (voidFallListener != null) {
            voidFallListener.reload();
        }
    }

    public String getColorString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }
}

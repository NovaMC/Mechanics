package xyz.novaserver.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.mechanics.features.Cleanable;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;
import xyz.novaserver.mechanics.features.chairs.ChairsFeature;
import xyz.novaserver.mechanics.features.launch_pads.LauchpadFeature;
import xyz.novaserver.mechanics.features.navigation_book.NavigationFeature;
import xyz.novaserver.mechanics.features.phone_menu.PhoneFeature;
import xyz.novaserver.mechanics.features.pinging.PingingFeature;
import xyz.novaserver.mechanics.features.portal_coords.PortalCoordsFeature;
import xyz.novaserver.mechanics.features.proxy_cmd.ProxyCommandFeature;
import xyz.novaserver.mechanics.features.void_fall.VoidFallFeature;

import java.util.HashSet;
import java.util.Set;

public class NovaMechanics extends JavaPlugin {

    private final Set<Feature> features = new HashSet<>();

    private void addIfEnabled(String key, Feature feature) {
        if (getConfig().getBoolean("features." + key)) {
            features.add(feature);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Enable features
        addIfEnabled("chairs", new ChairsFeature());
        addIfEnabled("launchpads", new LauchpadFeature());
        addIfEnabled("navigation-book", new NavigationFeature());
        addIfEnabled("pinging", new PingingFeature());
        addIfEnabled("portal-coords", new PortalCoordsFeature());
        addIfEnabled("void-fall", new VoidFallFeature());
        addIfEnabled("proxy-cmd", new ProxyCommandFeature());
        addIfEnabled("phone-menu", new PhoneFeature());

        // Register features
        for (Feature feature : features) {
            feature.register(this);
        }

        getCommand("novamech").setExecutor(new MechanicsCommand(this));
    }

    @Override
    public void onDisable() {
        // Cleanup any features that need it
        for (Feature feature : features) {
            if (feature instanceof Cleanable cleanable) {
                cleanable.clean(this);
            }
        }
    }

    public void reload() {
        reloadConfig();

        // Reload any features that need it
        for (Feature feature : features) {
            if (feature instanceof Reloadable reloadable) {
                reloadable.reload(this);
            }
        }
    }

    public String getColorString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }
}

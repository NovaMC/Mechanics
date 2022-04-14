package xyz.novaserver.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.mechanics.features.Cleanable;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;
import xyz.novaserver.mechanics.features.chairs.ChairsFeature;
import xyz.novaserver.mechanics.features.launch_pads.LauchpadsFeature;
import xyz.novaserver.mechanics.features.navigation_book.NavigationBookFeature;
import xyz.novaserver.mechanics.features.new_players.NewPlayersFeature;
import xyz.novaserver.mechanics.features.phone_menu.PhoneFeature;
import xyz.novaserver.mechanics.features.pinging.PingingFeature;
import xyz.novaserver.mechanics.features.portal_coords.PortalCoordsFeature;
import xyz.novaserver.mechanics.features.proxy_cmd.ProxyCmdFeature;
import xyz.novaserver.mechanics.features.void_fall.VoidFallFeature;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class NovaMechanics extends JavaPlugin {

    private final Set<Feature> features = new HashSet<>();

    private void addIfEnabled(String key, Class<? extends Feature> feature) throws ReflectiveOperationException {
        if (getConfig().getBoolean("features." + key)) {
            features.add(feature.getDeclaredConstructor().newInstance());
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Enable features
        try {
            addIfEnabled("chairs", ChairsFeature.class);
            addIfEnabled("launchpads", LauchpadsFeature.class);
            addIfEnabled("navigation-book", NavigationBookFeature.class);
            addIfEnabled("pinging", PingingFeature.class);
            addIfEnabled("portal-coords", PortalCoordsFeature.class);
            addIfEnabled("void-fall", VoidFallFeature.class);
            addIfEnabled("proxy-cmd", ProxyCmdFeature.class);
            addIfEnabled("phone", PhoneFeature.class);
            addIfEnabled("new-players", NewPlayersFeature.class);
        } catch (ReflectiveOperationException e) {
            getLogger().log(Level.SEVERE, "Reflection error occurred while trying to setup features", e);
        }

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

package xyz.novaserver.mechanics;

import com.google.common.base.CaseFormat;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import xyz.novaserver.mechanics.features.Cleanable;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;

import java.util.HashSet;
import java.util.Set;

public class NovaMechanics extends JavaPlugin {

    private final Set<Feature> features = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Load placeholders
        Reflections reflections = new Reflections("xyz.novaserver.mechanics.features");
        Set<Class<? extends Feature>> classes = reflections.getSubTypesOf(Feature.class);
        for (Class<? extends Feature> clazz : classes) {
            try {
                // Try to register the feature
                try {
                    registerIfEnabled(clazz);
                } catch (ReflectiveOperationException e) {
                    getSLF4JLogger().error("A reflection error occurred while trying to register features!", e);
                }
            } catch (NoClassDefFoundError e) {
                getSLF4JLogger().info("Not loading " + clazz.getSimpleName() + " because of missing classes!");
            }
        }

        //noinspection ConstantConditions
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

    private void registerIfEnabled(Class<? extends Feature> clazz) throws ReflectiveOperationException {
        // Convert class name to kebab-case
        String featureName = clazz.getSimpleName().replace("Feature", "");
        String configName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, featureName);

        // If feature is set to enabled create a new instance and register it
        if (getConfig().getBoolean("features." + configName, false)) {
            Feature feature = clazz.getDeclaredConstructor().newInstance();
            feature.register(this);
            features.add(feature);
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
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, ""));
    }
}

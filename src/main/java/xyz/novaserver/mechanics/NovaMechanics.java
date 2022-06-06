package xyz.novaserver.mechanics;

import com.google.common.base.CaseFormat;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import xyz.novaserver.mechanics.features.Cleanable;
import xyz.novaserver.mechanics.features.EarlyLoadable;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NovaMechanics extends JavaPlugin {

    private final Set<Class<? extends Feature>> toRegister = new HashSet<>();
    private final Set<Feature> enabledFeatures = new HashSet<>();

    @Override
    public void onLoad() {
        saveDefaultConfig();

        // Load feature classes
        Reflections reflections = new Reflections("xyz.novaserver.mechanics.features");
        Set<Class<? extends Feature>> classes = reflections.getSubTypesOf(Feature.class);

        for (Class<? extends Feature> clazz : classes) {
            // Load feature early if it implements EarlyLoadable
            if (EarlyLoadable.class.isAssignableFrom(clazz) && isEnabled(clazz)) {
                try {
                    Feature feature = clazz.getDeclaredConstructor().newInstance();
                    ((EarlyLoadable) feature).onLoad(this);
                    enabledFeatures.add(feature);
                } catch (ReflectiveOperationException | NoClassDefFoundError e) {
                    getSLF4JLogger().error("A reflection error occurred while trying to register features!", e);
                }
            }
            toRegister.add(clazz);
        }
    }

    @Override
    public void onEnable() {
        // Register features that don't load early
        for (Class<? extends Feature> clazz : toRegister) {
            try {
                // Add the feature to class set
                try {
                    registerIfEnabled(clazz);
                } catch (ReflectiveOperationException e) {
                    getSLF4JLogger().error("A reflection error occurred while trying to register features!", e);
                }
            } catch (NoClassDefFoundError e) {
                getSLF4JLogger().info("Not loading " + clazz.getSimpleName() + " because of missing classes!");
            }
        }
        // Clear toRegister as the stored classes aren't needed anymore
        toRegister.clear();

        //noinspection ConstantConditions
        getCommand("novamech").setExecutor(new MechanicsCommand(this));
    }

    @Override
    public void onDisable() {
        // Cleanup any features that need it
        for (Feature feature : enabledFeatures) {
            if (feature instanceof Cleanable cleanable) {
                cleanable.clean(this);
            }
        }
    }

    private boolean isEnabled(Class<? extends Feature> clazz) {
        // Convert class name to kebab-case
        String featureName = clazz.getSimpleName().replace("Feature", "");
        String configName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, featureName);
        return getConfig().getBoolean("features." + configName, false);
    }

    private void registerIfEnabled(Class<? extends Feature> clazz) throws ReflectiveOperationException {
        // If feature is set to enabled create a new instance and register it
        if (isEnabled(clazz)) {
            // Grab existing instance if it exists
            Optional<Feature> optionalFeature = enabledFeatures.stream().filter(clazz::isInstance).findFirst();
            Feature feature;
            if (optionalFeature.isPresent()) {
                feature = optionalFeature.get();
            } else {
                feature = clazz.getDeclaredConstructor().newInstance();
                enabledFeatures.add(feature);
            }
            feature.register(this);
        }
    }

    public void reload() {
        reloadConfig();

        // Reload any features that need it
        for (Feature feature : enabledFeatures) {
            if (feature instanceof Reloadable reloadable) {
                reloadable.reload(this);
            }
        }
    }

    public Set<Feature> getEnabledFeatures() {
        return enabledFeatures;
    }

    public String getColorString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, ""));
    }
}

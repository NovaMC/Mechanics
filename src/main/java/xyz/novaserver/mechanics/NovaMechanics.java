package xyz.novaserver.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.mechanics.features.Cleanable;
import xyz.novaserver.mechanics.features.EarlyLoaded;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;

public class NovaMechanics extends JavaPlugin {

    @Override
    public void onLoad() {
        saveDefaultConfig();

        // Enable features that need to load early
        for (FeatureRegistry feature : FeatureRegistry.values()) {
            if (enabledInConfig(feature.getFeatureName())
                    && EarlyLoaded.class.isAssignableFrom(feature.getFeatureClass())) {
                try {
                    getSLF4JLogger().info("Enabling feature early: " + feature.getFeatureName());
                    FeatureRegistry.enable(feature);
                    // Call onLoad function of EarlyLoaded after feature is enabled
                    ((EarlyLoaded) feature.getFeature()).onLoad(this);
                } catch (ReflectiveOperationException e) {
                    getSLF4JLogger().error("A reflection error occurred while trying to register features!", e);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // Enable features that don't load early
        for (FeatureRegistry feature : FeatureRegistry.values()) {
            if (enabledInConfig(feature.getFeatureName()) && feature.getFeature() == null) {
                try {
                    getSLF4JLogger().info("Enabling feature: " + feature.getFeatureName());
                    FeatureRegistry.enable(feature);
                    // Register the feature after enabling
                    feature.getFeature().register(this);
                } catch (ReflectiveOperationException e) {
                    getSLF4JLogger().error("A reflection error occurred while trying to register features!", e);
                }
            } else if (feature.getFeature() != null) {
                // Register any features that have been EarlyLoaded
                feature.getFeature().register(this);
            }
        }

        //noinspection ConstantConditions
        getCommand("novamech").setExecutor(new MechanicsCommand(this));
    }

    @Override
    public void onDisable() {
        // Cleanup any features that need it
        for (Feature feature : FeatureRegistry.getEnabledFeatures()) {
            if (feature instanceof Cleanable cleanable) {
                cleanable.clean(this);
            }
        }
    }

    public void reload() {
        reloadConfig();

        // Reload any features that need it
        for (Feature feature : FeatureRegistry.getEnabledFeatures()) {
            if (feature instanceof Reloadable reloadable) {
                reloadable.reload(this);
            }
        }
    }

    private boolean enabledInConfig(String featureName) {
        return getConfig().getBoolean("features." + featureName, false);
    }

    public String getColorString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, ""));
    }
}

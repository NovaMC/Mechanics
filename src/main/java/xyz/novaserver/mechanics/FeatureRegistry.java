package xyz.novaserver.mechanics;

import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.chairs.ChairsFeature;
import xyz.novaserver.mechanics.features.chatrooms.ChatroomsFeature;
import xyz.novaserver.mechanics.features.launch_pads.LaunchpadsFeature;
import xyz.novaserver.mechanics.features.navigation_book.NavigationBookFeature;
import xyz.novaserver.mechanics.features.new_players.NewPlayersFeature;
import xyz.novaserver.mechanics.features.phone_menu.PhoneFeature;
import xyz.novaserver.mechanics.features.portal_coords.PortalCoordsFeature;
import xyz.novaserver.mechanics.features.proxy_cmd.ProxyCmdFeature;
import xyz.novaserver.mechanics.features.void_fall.VoidFallFeature;

import java.util.*;
import java.util.stream.Collectors;

public enum FeatureRegistry {
    CHAIRS(ChairsFeature.class, "chairs"),
    CHATROOMS(ChatroomsFeature.class, "chatrooms"),
    LAUNCHPADS(LaunchpadsFeature.class, "launchpads"),
    NAVIGATION_BOOK(NavigationBookFeature.class, "navigation-book"),
    NEW_PLAYER(NewPlayersFeature.class, "new-players"),
    PHONE_MENU(PhoneFeature.class, "phone"),
    PORTAL_COORDS(PortalCoordsFeature.class, "portal-coords"),
    PROXY_CMD(ProxyCmdFeature.class, "proxy-cmd"),
    VOID_FALL(VoidFallFeature.class, "void-fall");

    public static void enable(FeatureRegistry registry) throws ReflectiveOperationException {
        registry.feature = registry.getFeatureClass().getDeclaredConstructor().newInstance();
    }

    public static Set<Feature> getEnabledFeatures() {
        return Arrays.stream(values())
                .filter(feature -> feature.getFeatureClass() != null)
                .map(FeatureRegistry::getFeature)
                .collect(Collectors.toUnmodifiableSet());
    }

    private final Class<? extends Feature> featureClass;
    private final String featureName;
    private Feature feature;

    FeatureRegistry(Class<? extends Feature> featureClass, String featureName) {
        this.featureClass = featureClass;
        this.featureName = featureName;
    }

    public Class<? extends Feature> getFeatureClass() {
        return featureClass;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Feature getFeature() {
        return feature;
    }
}

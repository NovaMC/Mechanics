package xyz.novaserver.mechanics.features.chatrooms;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.EarlyLoaded;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;
import xyz.novaserver.mechanics.features.chatrooms.listener.ChatroomsListener;
import xyz.novaserver.mechanics.features.chatrooms.listener.DiscordListener;
import xyz.novaserver.mechanics.features.chatrooms.util.ChatroomUtils;
import xyz.novaserver.mechanics.features.chatrooms.util.TitleData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatroomsFeature implements Feature, EarlyLoaded, Reloadable {
    private StringFlag chatroomFlag;
    private YamlConfiguration config;
    private TitleData titleData;

    private final Map<String, Chatroom> chatrooms = new HashMap<>();
    private final Map<UUID, Chatroom> playerMap = new HashMap<>();

    public Map<String, Chatroom> chatrooms() {
        return chatrooms;
    }

    public Map<UUID, Chatroom> playerMap() {
        return playerMap;
    }

    public StringFlag chatroomFlag() {
        return chatroomFlag;
    }

    public YamlConfiguration config() {
        return config;
    }

    public TitleData titleData() {
        return titleData;
    }

    @Override
    public void onLoad(NovaMechanics mechanics) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StringFlag flag = new StringFlag("chatroom");
            registry.register(flag);
            chatroomFlag = flag;
        } catch (FlagConflictException e) {
            mechanics.getSLF4JLogger().error("Failed to register chatroom flag! Check for conflicting plugins.", e);
        }
    }

    @Override
    public void register(NovaMechanics mechanics) {
        PluginManager pluginManager = mechanics.getServer().getPluginManager();
        if (!pluginManager.isPluginEnabled("NovaPlaceholders")) {
            return;
        }
        titleData = new TitleData();
        reload(mechanics);
        pluginManager.registerEvents(new ChatroomsListener(this), mechanics);
        // Only register discord listener if the plugin is enabled
        if (pluginManager.isPluginEnabled("EssentialsDiscord")) {
            pluginManager.registerEvents(new DiscordListener(this), mechanics);
        }
    }

    @Override
    public void reload(NovaMechanics mechanics) {
        config = loadConfig(mechanics);
        if (config == null) {
            return;
        }
        chatrooms.clear();

        ConfigurationSection chatrooms = config.getConfigurationSection("chatrooms");
        if (chatrooms != null) {
            chatrooms.getKeys(false).stream()
                    .map(s -> config.getConfigurationSection("chatrooms." + s))
                    .forEach(section -> {
                        if (section != null) {
                            Chatroom chatroom = parseChatroom(section);
                            this.chatrooms.put(chatroom.getId(), chatroom);
                        }
                    });
        }
        titleData.reload(config);
    }

    private YamlConfiguration loadConfig(NovaMechanics mechanics) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            File file = new File(mechanics.getDataFolder(), "chatrooms.yml");
            if(!file.exists()) {
                mechanics.saveResource("chatrooms.yml", false);
            }
            config.load(file);
        }
        catch (InvalidConfigurationException | IOException e) {
            mechanics.getSLF4JLogger().error("Failed to load chatroom configuration!", e);
            return null;
        }
        return config;
    }

    private Chatroom parseChatroom(ConfigurationSection section) {
        Component name = ChatroomUtils.asComponent(section.getString("name", ""));
        return new Chatroom(section.getName(), name);
    }
}

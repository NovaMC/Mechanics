package xyz.novaserver.mechanics.features.chatrooms;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.EarlyLoadable;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.features.Reloadable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatroomsFeature implements Feature, EarlyLoadable, Reloadable {
    private StringFlag chatRoomFlag;
    private final Map<String, Chatroom> chatroomMap = new HashMap<>();
    private YamlConfiguration config;
    private TitleData titleData;

    @Override
    public void onLoad(NovaMechanics mechanics) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StringFlag flag = new StringFlag("chatroom", "undefined");
            registry.register(flag);
            chatRoomFlag = flag;
        } catch (FlagConflictException e) {
            mechanics.getSLF4JLogger().error("Failed to register chatroom flag! Check for conflicting plugins.", e);
        }
    }

    @Override
    public void register(NovaMechanics mechanics) {
        if (!mechanics.getServer().getPluginManager().isPluginEnabled("NovaPlaceholders")) {
            return;
        }
        titleData = new TitleData();
        reload(mechanics);
        mechanics.getServer().getPluginManager().registerEvents(new ChatroomsListener(this), mechanics);
    }

    public Map<String, Chatroom> getChatroomMap() {
        return chatroomMap;
    }

    public StringFlag getChatRoomFlag() {
        return chatRoomFlag;
    }

    @Override
    public void reload(NovaMechanics mechanics) {
        config = loadConfig(mechanics);
        if (config == null) {
            return;
        }
        chatroomMap.clear();
        config.getConfigurationSection("chatrooms").getKeys(false).stream()
                .map(s -> config.getConfigurationSection("chatrooms." + s))
                .forEach(section -> {
                    if (section != null) {
                        Chatroom chatroom = parseChatroom(section);
                        chatroomMap.put(chatroom.getId(), chatroom);
                    }
                });
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

    public YamlConfiguration getConfig() {
        return config;
    }

    public TitleData getTitleData() {
        return titleData;
    }

    private Chatroom parseChatroom(ConfigurationSection section) {
        MiniMessage mm = MiniMessage.miniMessage();
        Component name = mm.deserialize(section.getString("name", ""));
        Component joinTitle = mm.deserialize(section.getString("join-title", ""));
        Component leaveTitle = mm.deserialize(section.getString("leave-title", ""));
        return new Chatroom(section.getName(), name, joinTitle, leaveTitle);
    }
}

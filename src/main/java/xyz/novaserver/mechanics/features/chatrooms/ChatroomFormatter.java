package xyz.novaserver.mechanics.features.chatrooms;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.placeholders.paper.chat.format.Formatter;
import xyz.novaserver.placeholders.paper.chat.util.MetaUtils;

public class ChatroomFormatter implements Formatter {
    private final ChatroomsFeature chatroomsFeature;

    public ChatroomFormatter(ChatroomsFeature chatroomsFeature) {
        this.chatroomsFeature = chatroomsFeature;
    }

    @Override
    public Component get(Player source, Component content, Audience viewer) {
        final ConfigurationSection config = chatroomsFeature.getConfig().getConfigurationSection("options.chat");

        boolean isBedrock = false;
        if (viewer instanceof Player playerViewer) {
            isBedrock = FloodgateApi.getInstance().isFloodgatePlayer(playerViewer.getUniqueId());
        }

        Component prefix;
        TextColor nameColor;
        TextColor messageColor;

        if (!isBedrock) {
            prefix = MetaUtils.asComponent(config.getString("java.prefix", ""));
            nameColor = MetaUtils.asComponent(config.getString("java.name-color", "")).color();
            messageColor = MetaUtils.asComponent(config.getString("java.message-color", "")).color();
        } else  {
            prefix = MetaUtils.asComponent(config.getString("bedrock.prefix", ""));
            nameColor = MetaUtils.asComponent(config.getString("bedrock.name-color", "")).color();
            messageColor = MetaUtils.asComponent(config.getString("bedrock.message-color", "")).color();
        }

        Component separator = Component.text(": ").color(nameColor);
        return prefix
                .append(source.displayName().color(nameColor))
                .append(separator)
                .append(content.color(messageColor));
    }
}

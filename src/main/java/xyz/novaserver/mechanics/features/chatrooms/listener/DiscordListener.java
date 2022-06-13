package xyz.novaserver.mechanics.features.chatrooms.listener;

import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.novaserver.mechanics.features.chatrooms.ChatroomsFeature;

public class DiscordListener implements Listener {
    private final ChatroomsFeature feature;

    public DiscordListener(ChatroomsFeature feature) {
        this.feature = feature;
    }

    @EventHandler
    public void onDiscordChat(DiscordChatMessageEvent event) {
        // Block EssentialsDiscord messages from being sent if they originate from a chatroom
        if (feature.playerMap().containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}

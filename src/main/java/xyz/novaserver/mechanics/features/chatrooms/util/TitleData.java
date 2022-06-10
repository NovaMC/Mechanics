package xyz.novaserver.mechanics.features.chatrooms.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.mechanics.features.chatrooms.Chatroom;

import java.util.Objects;

@SuppressWarnings("PatternValidation")
public class TitleData {
    private static final FloodgateApi floodgate = FloodgateApi.getInstance();

    private Sound joinSound;
    private Sound leaveSound;
    private Title.Times times;
    private Component joinTitle;
    private Component joinSubtitle;
    private Component joinTitleBedrock;
    private Component joinSubtitleBedrock;
    private Component leaveTitle;
    private Component leaveSubtitle;
    private Component leaveTitleBedrock;
    private Component leaveSubtitleBedrock;

    public void reload(YamlConfiguration config) {
        // Load sounds
        joinSound = Sound.sound(Key.key(Objects.requireNonNull(config.getString("options.join.sound"))),
                Sound.Source.valueOf(config.getString("options.join.category")),
                (float) config.getDouble("options.join.volume"),
                (float) config.getDouble("options.join.pitch"));
        leaveSound = Sound.sound(Key.key(Objects.requireNonNull(config.getString("options.leave.sound"))),
                Sound.Source.valueOf(config.getString("options.leave.category")),
                (float) config.getDouble("options.leave.volume"),
                (float) config.getDouble("options.leave.pitch"));

        // Load titles
        times = Title.Times.times(Ticks.duration(10), Ticks.duration(30), Ticks.duration(10));

        joinTitle = ChatroomUtils.asComponent(config.getString("options.join.title.java"));
        joinSubtitle = ChatroomUtils.asComponent(config.getString("options.join.subtitle.java"));
        joinTitleBedrock = ChatroomUtils.asComponent(config.getString("options.join.title.bedrock"));
        joinSubtitleBedrock = ChatroomUtils.asComponent(config.getString("options.join.subtitle.bedrock"));

        leaveTitle = ChatroomUtils.asComponent(config.getString("options.leave.title.java"));
        leaveSubtitle = ChatroomUtils.asComponent(config.getString("options.leave.subtitle.java"));
        leaveTitleBedrock = ChatroomUtils.asComponent(config.getString("options.leave.title.bedrock"));
        leaveSubtitleBedrock = ChatroomUtils.asComponent(config.getString("options.leave.subtitle.bedrock"));
    }

    public void sendJoin(Player player, Chatroom chatroom) {
        if (player == null) return;
        // play sound
        player.playSound(joinSound);
        // check for bedrock
        boolean isBedrock = floodgate.isFloodgatePlayer(player.getUniqueId());
        Title title;
        if (!isBedrock) {
            title = Title.title(ChatroomUtils.replacePlaceholders(chatroom, joinTitle),
                    ChatroomUtils.replacePlaceholders(chatroom, joinSubtitle), times);
        } else {
            title = Title.title(ChatroomUtils.replacePlaceholders(chatroom, joinTitleBedrock),
                    ChatroomUtils.replacePlaceholders(chatroom, joinSubtitleBedrock), times);
        }
        player.showTitle(title);
    }

    public void sendLeave(Player player, Chatroom chatroom) {
        if (player == null) return;
        // play sound
        player.playSound(leaveSound);
        // check for bedrock
        boolean isBedrock = floodgate.isFloodgatePlayer(player.getUniqueId());
        Title title;
        if (!isBedrock) {
            title = Title.title(ChatroomUtils.replacePlaceholders(chatroom, leaveTitle),
                    ChatroomUtils.replacePlaceholders(chatroom, leaveSubtitle), times);
        } else {
            title = Title.title(ChatroomUtils.replacePlaceholders(chatroom, leaveTitleBedrock),
                    ChatroomUtils.replacePlaceholders(chatroom, leaveSubtitleBedrock), times);
        }
        player.showTitle(title);
    }
}

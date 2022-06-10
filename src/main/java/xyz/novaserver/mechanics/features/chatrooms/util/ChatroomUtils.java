package xyz.novaserver.mechanics.features.chatrooms.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.novaserver.mechanics.features.chatrooms.Chatroom;

public class ChatroomUtils {
    public static Component replacePlaceholders(Chatroom chatroom, Component component) {
        TextReplacementConfig.Builder builder = TextReplacementConfig.builder()
                .matchLiteral("<chatroom>")
                .times(1000)
                .replacement(chatroom.getName());
        return component.replaceText(builder.build());
    }

    public static Component asComponent(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }
}

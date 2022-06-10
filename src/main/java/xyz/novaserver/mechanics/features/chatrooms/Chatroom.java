package xyz.novaserver.mechanics.features.chatrooms;

import net.kyori.adventure.text.Component;


public class Chatroom {
    private final String id;
    private final Component name;

    public Chatroom(String id, Component name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }
}

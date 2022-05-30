package xyz.novaserver.mechanics.features.chatrooms;

import net.kyori.adventure.text.Component;


public class Chatroom {
    private final String id;
    private final Component name;
    private final Component joinTitle;
    private final Component leaveTitle;

    public Chatroom(String id, Component name, Component joinTitle, Component leaveTitle) {
        this.id = id;
        this.name = name;
        this.joinTitle = joinTitle;
        this.leaveTitle = leaveTitle;
    }

    public String getId() {
        return id;
    }

    public Component getName() {
        return name;
    }

    public Component getJoinTitle() {
        return joinTitle;
    }

    public Component getLeaveTitle() {
        return leaveTitle;
    }
}

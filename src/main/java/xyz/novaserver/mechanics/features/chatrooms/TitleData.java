package xyz.novaserver.mechanics.features.chatrooms;

import org.bukkit.configuration.file.YamlConfiguration;

public class TitleData {

    private String joinSound;
    private String leaveSound;
    private String soundCategory;
    private float volume;
    private float pitch;
    private String joinMessage;
    private String joinSubMessage;
    private String joinMessageBedrock;
    private String joinSubMessageBedrock;
    private String leaveMessage;
    private String leaveSubMessage;
    private String leaveMessageBedrock;
    private String leaveSubMessageBedrock;

    public void reload(YamlConfiguration config) {
        joinSound = config.getString("options.join.sound");
        leaveSound = config.getString("options.leave.sound");
        soundCategory = config.getString("options.join.category");
        volume = (float) config.getDouble("options.join.volume");
        pitch = (float) config.getDouble("options.join.pitch");
        joinMessage = config.getString("options.join.title.java");
        joinSubMessage = config.getString("options.join.subtitle.java");
        joinMessageBedrock = config.getString("options.join.title.bedrock");
        joinSubMessageBedrock = config.getString("options.join.subtitle.bedrock");
        leaveMessage = config.getString("options.leave.title.java");
        leaveSubMessage = config.getString("options.leave.subtitle.java");
        leaveMessageBedrock = config.getString("options.leave.title.bedrock");
        leaveSubMessageBedrock = config.getString("options.leave.subtitle.bedrock");
    }

    public String getJoinSound() {
        return joinSound;
    }

    public String getLeaveSound() {
        return leaveSound;
    }

    public String getSoundCategory() {
        return soundCategory;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getJoinSubMessage() {
        return joinSubMessage;
    }

    public String getJoinMessageBedrock() {
        return joinMessageBedrock;
    }

    public String getJoinSubMessageBedrock() {
        return joinSubMessageBedrock;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public String getLeaveSubMessage() {
        return leaveSubMessage;
    }

    public String getLeaveMessageBedrock() {
        return leaveMessageBedrock;
    }

    public String getLeaveSubMessageBedrock() {
        return leaveSubMessageBedrock;
    }
}

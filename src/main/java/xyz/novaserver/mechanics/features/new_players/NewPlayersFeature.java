package xyz.novaserver.mechanics.features.new_players;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.novaserver.cutscenes.Cutscenes;
import xyz.novaserver.cutscenes.cutscene.Animation;
import xyz.novaserver.cutscenes.cutscene.Cutscene;
import xyz.novaserver.cutscenes.cutscene.Frame;
import xyz.novaserver.cutscenes.cutscene.Transition;
import xyz.novaserver.cutscenes.cutscene.camera.Camera;
import xyz.novaserver.cutscenes.cutscene.camera.PacketCamera;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class NewPlayersFeature implements Feature {
    private NovaMechanics mechanics;
    private FileConfiguration config;

    private final List<Animation> animationList = new ArrayList<>();
    private long duration;

    @Override
    public void register(NovaMechanics mechanics) {
        this.mechanics = mechanics;
        this.config = mechanics.getConfig();
        if (mechanics.getServer().getPluginManager().isPluginEnabled("NovaCutscenes")) {
            mechanics.getServer().getPluginManager().registerEvents(new CutsceneListener(this), mechanics);
            mechanics.getCommand("playspawnscene").setExecutor(new SpawnSceneCommand(this));

            this.duration = config.getLong("new-players.cutscene.duration", 20);

            // Preload animations
            config.getStringList("new-players.cutscene.names")
                    .stream()
                    .map(s -> s + ".yml")
                    .forEach(file -> {
                        try {
                            Animation animation = Animation.parse(new File(Cutscenes.getInstance().getAnimationFolder(), file));
                            animation.setTransition(Transition.fade(duration, 0, duration));
                            animationList.add(animation);
                        } catch (IOException e) {
                            mechanics.getLogger().log(Level.WARNING, "Failed to load " + file + ", does it exist?");
                        }
                    });
        }
    }

    public void playCutscene(Player player) {
        final Location originalLoc = player.getLocation();
        final String sound = config.getString("new-players.cutscene.sound", "null");
        final SoundCategory category = SoundCategory.valueOf(config.getString("new-players.cutscene.category", "RECORDS"));
        final Transition startTransition = config.getBoolean("new-players.cutscene.fade", true)
                ? Transition.fade(0, 0, duration) : Transition.cut();

        // Preparation before cutscene
        player.stopSound(sound, category);
        player.playSound(originalLoc, sound, category,
                (float) config.getDouble("new-players.cutscene.volume", 1.0D),
                (float) config.getDouble("new-players.cutscene.pitch", 1.0D));

        final Camera camera = new PacketCamera(player);
        camera.setup();
        new Cutscene(startTransition, animationList.toArray(new Animation[0])).play(camera, () -> {
            camera.destroy();
            camera.sendFrame(new Frame(originalLoc.toVector(), originalLoc.getYaw(), originalLoc.getPitch()));
        });
    }

    public NovaMechanics getMechanics() {
        return mechanics;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}

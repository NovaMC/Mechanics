package xyz.novaserver.mechanics.features.new_players;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpawnSceneCommand implements TabExecutor {
    private final NewPlayersFeature feature;

    public SpawnSceneCommand(NewPlayersFeature feature) {
        this.feature = feature;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return true;

        Player player = Bukkit.getPlayer(args[0]);
        if (player != null) feature.playCutscene(player);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Stream<String> possibilities = Bukkit.getOnlinePlayers().stream().map(Player::getName);
        if (args.length == 0) {
            return possibilities.collect(Collectors.toList());
        } else if (args.length == 1) {
            return possibilities
                    .filter(name -> name.regionMatches(true, 0, args[0], 0, args[0].length()))
                    .collect(Collectors.toList());
        } else {
            return ImmutableList.of();
        }
    }
}

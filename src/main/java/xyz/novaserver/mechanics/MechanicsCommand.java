package xyz.novaserver.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MechanicsCommand implements TabExecutor {

    private final NovaMechanics plugin;

    public MechanicsCommand(NovaMechanics plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mechanics.admin")) {
            sender.sendMessage(plugin.getColorString("no-permission"));
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config file!");
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GREEN + "Currently enabled features:");
                FeatureRegistry.getEnabledFeatures().forEach(feature -> {
                    sender.sendMessage(ChatColor.GREEN + feature.getClass().getSimpleName());
                });
            }
        }
        else {
            sender.sendMessage(ChatColor.YELLOW + "Type /novamech reload to reload the config.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1 || !sender.hasPermission("mechanics.admin")) {
            return Collections.emptyList();
        }

        final List<String> possibilities = Arrays.asList("reload", "list");
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], possibilities, completions);

        return completions;
    }
}

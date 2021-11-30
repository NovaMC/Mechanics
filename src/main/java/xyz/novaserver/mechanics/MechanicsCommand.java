package xyz.novaserver.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config file!");
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

        final List<String> possibilities = Collections.singletonList("reload");
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], possibilities, completions);

        return completions;
    }
}

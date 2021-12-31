package xyz.novaserver.mechanics.features.proxy_cmd;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SendProxyCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final String CHANNEL_NAME = "mechanics:command";

    public SendProxyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_NAME);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) return true;

        String cmd = String.join(" ", args);
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Console");
        out.writeUTF(cmd);

        if (player != null) player.sendPluginMessage(plugin, CHANNEL_NAME, out.toByteArray());

        return true;
    }
}

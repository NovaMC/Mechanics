package xyz.novaserver.mechanics.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.novaserver.mechanics.NovaMechanics;

import java.util.Collections;
import java.util.List;

public class PortalCoordsCommand implements TabExecutor {

    private final NovaMechanics plugin;

    public PortalCoordsCommand(NovaMechanics plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        if (!sender.hasPermission("mechanics.portalcoords")) {
            sender.sendMessage(plugin.getColorString("no-permission"));
            return true;
        }

        Player player = (Player) sender;
        Location newLocation = player.getLocation();
        String message;

        if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            newLocation.setX(player.getLocation().getX() / 8);
            newLocation.setZ(player.getLocation().getZ() / 8);
            message = plugin.getColorString("portalcoords.overworld-coords");
        }
        else if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            newLocation.setX(player.getLocation().getX() * 8);
            newLocation.setZ(player.getLocation().getZ() * 8);
            message = plugin.getColorString("portalcoords.nether-coords");
        }
        else {
            message = ChatColor.RED + "You are in an unsupported world!";
        }

        if (message != null && !message.isEmpty()) {
            String finalMessage = message.replace("%x", String.valueOf((int) newLocation.getX()))
                    .replace("%y", String.valueOf((int) newLocation.getY()))
                    .replace("%z", String.valueOf((int) newLocation.getZ()));
            sender.sendMessage(finalMessage);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}

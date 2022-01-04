package xyz.novaserver.mechanics.features.phone_menu;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GivePhoneCommand implements TabExecutor {

    private final PhoneFeature feature;

    public GivePhoneCommand(PhoneFeature feature) {
        this.feature = feature;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "The phone can only be used by java players!");
            return true;
        }

        String message;
        if (feature.givePlayerPhone(player)) {
            message = feature.getMechanics().getConfig().getString("phone.take-out", "");
        } else {
            Arrays.stream(player.getInventory().getContents())
                    .filter(item -> ItemUtils.instanceOf(item, feature.TEST_ITEM, feature.getMechanics()))
                    .forEach(item -> player.getInventory().remove(item));
            player.getPersistentDataContainer().set(feature.PHONE_KEY, PersistentDataType.BYTE, (byte) 1);
            message = feature.getMechanics().getConfig().getString("phone.put-away", "");
        }

        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}

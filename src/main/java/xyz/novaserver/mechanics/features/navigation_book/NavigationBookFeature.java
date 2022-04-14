package xyz.novaserver.mechanics.features.navigation_book;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.Arrays;
import java.util.HashMap;

public class NavigationBookFeature implements Feature {

    protected NavigationBook TEST_ITEM;
    private NovaMechanics mechanics;
    private FloodgateApi floodgateApi;

    @Override
    public void register(NovaMechanics mechanics) {
        // Book needs floodgate & essentials present to function
        if (!mechanics.getServer().getPluginManager().isPluginEnabled("floodgate") ||
                !mechanics.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            return;
        }

        TEST_ITEM = new NavigationBook(mechanics);
        this.mechanics = mechanics;
        this.floodgateApi = FloodgateApi.getInstance();

        // Register events and commands
        mechanics.getServer().getPluginManager().registerEvents(new NavigationListener(this), mechanics);
    }

    protected NovaMechanics getMechanics() {
        return mechanics;
    }

    protected FloodgateApi getFloodgateApi() {
        return floodgateApi;
    }

    protected void givePlayerBook(Player player) {
        // Remove book if player is joining from java & return
        if (!floodgateApi.isFloodgatePlayer(player.getUniqueId())) {
            player.getInventory().forEach(item -> {
                if (ItemUtils.instanceOf(item, TEST_ITEM, getMechanics()))
                    player.getInventory().remove(item);
            });
            return;
        }

        // Return if player already has item
        if (Arrays.stream(player.getInventory().getContents())
                .anyMatch(item -> ItemUtils.instanceOf(item, TEST_ITEM, mechanics))) {
            return;
        }

        final int SLOT = 8;
        ItemStack slotItem = player.getInventory().getItem(SLOT);

        // If slot is empty put in the slot
        if (slotItem == null || slotItem.getType() == Material.AIR) {
            player.getInventory().setItem(SLOT, new NavigationBook(mechanics));
        }
        // Else give item and move item in hotbar slot
        else if (!ItemUtils.instanceOf(slotItem, TEST_ITEM, mechanics)) {
            // Replace old item with book
            player.getInventory().setItem(SLOT, new NavigationBook(mechanics));

            // Add old item back to inventory
            HashMap<Integer, ItemStack> itemMap = player.getInventory().addItem(slotItem);

            // Try to give player old item by dropping if inventory is full
            if (!itemMap.isEmpty()) {
                itemMap.forEach((integer, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
            }
        }
    }
}

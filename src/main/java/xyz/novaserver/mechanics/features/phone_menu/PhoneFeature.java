package xyz.novaserver.mechanics.features.phone_menu;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.features.Feature;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.Arrays;
import java.util.HashMap;

public class PhoneFeature implements Feature {

    protected PhoneItem TEST_ITEM;
    protected NamespacedKey PHONE_KEY;
    private NovaMechanics mechanics;

    @Override
    public void register(NovaMechanics mechanics) {
        TEST_ITEM = new PhoneItem(mechanics);
        PHONE_KEY = new NamespacedKey(mechanics, "no_phone");
        this.mechanics = mechanics;

        mechanics.getServer().getPluginManager().registerEvents(new PhoneListener(this), mechanics);
        mechanics.getCommand("phone").setExecutor(new GivePhoneCommand(this));
    }

    protected NovaMechanics getMechanics() {
        return mechanics;
    }

    protected boolean givePlayerPhone(Player player) {
        // Return if player is on bedrock
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) return false;

        // Return if player already has item
        if (Arrays.stream(player.getInventory().getContents())
                .anyMatch(item -> ItemUtils.instanceOf(item, TEST_ITEM, mechanics))) {
            return false;
        }

        final int SLOT = 8;
        ItemStack slotItem = player.getInventory().getItem(SLOT);

        // If slot is empty put in the slot
        if (slotItem == null || slotItem.getType() == Material.AIR) {
            player.getInventory().setItem(SLOT, new PhoneItem(mechanics));
            return true;
        }
        // Else give item and move item in hotbar slot
        else if (!ItemUtils.instanceOf(slotItem, TEST_ITEM, mechanics)) {
            // Replace old item with book
            player.getInventory().setItem(SLOT, new PhoneItem(mechanics));

            // Add old item back to inventory
            HashMap<Integer, ItemStack> itemMap = player.getInventory().addItem(slotItem);

            // Try to give player old item by dropping if inventory is full
            if (!itemMap.isEmpty()) {
                itemMap.forEach((integer, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
            }
            return true;
        }
        return false;
    }
}

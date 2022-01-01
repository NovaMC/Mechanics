package xyz.novaserver.mechanics.features.phone_menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.Arrays;
import java.util.HashMap;

public class PhoneListener implements Listener {

    private final NovaMechanics plugin;
    private final PhoneItem testItem;

    public PhoneListener(NovaMechanics plugin) {
        this.plugin = plugin;
        this.testItem = new PhoneItem(plugin);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if (event.hasItem() && ItemUtils.instanceOf(event.getItem(), testItem, plugin)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.getPlayer().performCommand("menu");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        givePlayerItem(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePlayerItem(event.getPlayer());
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent event) {
        Inventory topInv = event.getInventory();
        if (ItemUtils.instanceOf(event.getOldCursor(), testItem, plugin)
                && (topInv.getType() != InventoryType.PLAYER && topInv.getType() != InventoryType.CRAFTING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        Inventory topInv = event.getInventory();
        if ((inv != null && inv.getType() != InventoryType.PLAYER) || (topInv.getType() != InventoryType.PLAYER && topInv.getType() != InventoryType.CRAFTING)) {
            if (ItemUtils.instanceOf(event.getCursor(), testItem, plugin) || ItemUtils.instanceOf(event.getCurrentItem(), testItem, plugin)) {
                event.setCancelled(true);
            } else if (event.getClick() == ClickType.NUMBER_KEY) {
                ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                if (ItemUtils.instanceOf(item, testItem, plugin)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (ItemUtils.instanceOf(event.getItemDrop().getItemStack(), testItem, plugin)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> ItemUtils.instanceOf(item, testItem, plugin));
    }

    private void givePlayerItem(Player player) {
        final int SLOT = 8;
        ItemStack slotItem = player.getInventory().getItem(SLOT);

        // Return if player already has item
        if (Arrays.stream(player.getInventory().getContents())
                .anyMatch(item -> ItemUtils.instanceOf(item, testItem, plugin))) {
            return;
        }

        // Give item and move item in hotbar slot
        if (slotItem != null && !ItemUtils.instanceOf(slotItem, testItem, plugin)) {
            // Replace old item with book
            player.getInventory().setItem(SLOT, new PhoneItem(plugin));

            // Add old item back to inventory
            HashMap<Integer, ItemStack> itemMap = player.getInventory().addItem(slotItem);

            // Try to give player old item by dropping if inventory is full
            if (!itemMap.isEmpty()) {
                itemMap.forEach((integer, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
            }
        }
        // If slot is empty just put the book in the slot
        else if (slotItem == null || slotItem.getType() == Material.AIR) {
            player.getInventory().setItem(SLOT, new PhoneItem(plugin));
        }
    }
}

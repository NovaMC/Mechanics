package xyz.novaserver.mechanics.features.phone_menu;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.item.ItemUtils;

public class PhoneListener implements Listener {

    private final PhoneFeature feature;
    private final NovaMechanics plugin;

    protected PhoneListener(PhoneFeature feature) {
        this.feature = feature;
        this.plugin = feature.getMechanics();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getPersistentDataContainer().getOrDefault(feature.PHONE_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 0) {
            feature.givePlayerPhone(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getPersistentDataContainer().getOrDefault(feature.PHONE_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 0) {
            feature.givePlayerPhone(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> ItemUtils.instanceOf(item, feature.TEST_ITEM, plugin));
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        if (event.hasItem() && ItemUtils.instanceOf(event.getItem(), feature.TEST_ITEM, plugin)) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.getPlayer().performCommand(plugin.getConfig().getString("phone.command", ""));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (ItemUtils.instanceOf(event.getItemDrop().getItemStack(), feature.TEST_ITEM, plugin)) {
            event.getItemDrop().remove();
            // Set no_phone flag so we don't accidentally give the player their phone back
            player.getPersistentDataContainer().set(feature.PHONE_KEY, PersistentDataType.BYTE, (byte) 1);

            String message = plugin.getConfig().getString("phone.put-away", "");
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent event) {
        if (ItemUtils.instanceOf(event.getOldCursor(), feature.TEST_ITEM, plugin) && event.getInventory().getType() != InventoryType.CRAFTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Inventory clickInv = event.getClickedInventory();

        if (ItemUtils.instanceOf(event.getCursor(), feature.TEST_ITEM, plugin)) {
            // If the item held on the cursor is placed outside the players inventory
            if (clickInv != null && clickInv.getType() != InventoryType.PLAYER) {
                event.setCancelled(true);
            }
        }
        else if (ItemUtils.instanceOf(event.getCurrentItem(), feature.TEST_ITEM, plugin)) {
            // If item in slot is a phone and the players to move it out of their inventory
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && clickInv != null && clickInv.getType() == InventoryType.PLAYER) {
                event.setCancelled(true);
            }
        }
        // Check for hotbar item if neither items are a phone
        else if (event.getClick() == ClickType.NUMBER_KEY) {
            // Get hotbar item using pressed key
            ItemStack numItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            // Check if item is a phone and player did not click in their inventory
            if (ItemUtils.instanceOf(numItem, feature.TEST_ITEM, plugin) && clickInv != null && clickInv.getType() != InventoryType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }
}

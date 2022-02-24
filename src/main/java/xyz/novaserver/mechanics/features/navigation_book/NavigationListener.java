package xyz.novaserver.mechanics.features.navigation_book;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.forms.CustomMenuForm;
import xyz.novaserver.mechanics.forms.SimpleMenuForm;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.List;

public class NavigationListener implements Listener {

    private final NavigationFeature feature;
    private final NovaMechanics plugin;

    private final FloodgateApi floodgate;
    private final Essentials essentials;

    public NavigationListener(NavigationFeature feature) {
        this.feature = feature;
        this.plugin = feature.getMechanics();
        this.floodgate = feature.getFloodgateApi();
        this.essentials = (Essentials) this.plugin.getServer().getPluginManager().getPlugin("Essentials");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        feature.givePlayerBook(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        feature.givePlayerBook(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Remove book from drops
        event.getDrops().removeIf(item -> ItemUtils.instanceOf(item, feature.TEST_ITEM, plugin));
    }

    @EventHandler
    public void onNavigationInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!floodgate.isFloodgatePlayer(player.getUniqueId())) {
            return;
        }

        // Check if the player clicks and the item is a navigation book
        if (event.hasItem() && ItemUtils.instanceOf(event.getItem(), feature.TEST_ITEM, plugin)
                && event.getAction() != Action.PHYSICAL) {
            // Cancel event and send player the main form
            floodgate.getPlayer(player.getUniqueId()).sendForm(getForms(player));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        // Cancel dropping item if its a book
        if (ItemUtils.instanceOf(event.getItemDrop().getItemStack(), feature.TEST_ITEM, plugin)) {
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
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && clickInv != null
                    && clickInv.getType() == InventoryType.PLAYER && event.getInventory().getType() != InventoryType.CRAFTING) {
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

    private SimpleForm getForms(Player player) {
        User essUser = essentials.getUser(player.getUniqueId());
        boolean hasTpRequest = essUser.getNextTpaRequest(false, false, false) != null;

        // Main menu form
        SimpleMenuForm menuForm = new SimpleMenuForm(Component.text("Navigation Book").color(NamedTextColor.LIGHT_PURPLE), Component.empty());
        // Sub-menu forms
        SimpleMenuForm playersForm = new SimpleMenuForm(menuForm, Component.text("Players").color(NamedTextColor.BLUE));
        SimpleMenuForm teleportsForm = new SimpleMenuForm(menuForm, Component.text("Locations").color(NamedTextColor.DARK_AQUA), Component.text("Choose a teleport location."));
        SimpleMenuForm homesForm = new SimpleMenuForm(menuForm, Component.text("Homes").color(NamedTextColor.RED), Component.text("Manage your homes."));
        SimpleMenuForm settingsForm = new SimpleMenuForm(menuForm, Component.text("Settings"), Component.text("Change server settings."));

        // Players start
        // Add list of player buttons for players form
        List<String> playerNames = essentials.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player)).map(HumanEntity::getName).toList();
        if (!playerNames.isEmpty()) {
            playersForm.setContent(Component.text("Choose a player to teleport to."));
        } else {
            playersForm.setContent(Component.text("There are no players online."));
        }
        playerNames.forEach(p -> playersForm.addSimpleButton(Component.text(p), () -> player.performCommand("tpa " + p)));
        // Players end

        // Locations start
        // Add locations to the location menu
        teleportsForm.addSimpleButton(Component.text("Spawn"), () -> player.performCommand("spawn"))
                .addSimpleButton(Component.text("Wild/Random"), () -> player.performCommand("wild"));
        // Locations end

        // Homes start
        List<String> homes = essUser.getHomes();
        CustomMenuForm addHome = new CustomMenuForm(homesForm, Component.text("Add Home").color(NamedTextColor.DARK_GREEN));
        CustomMenuForm deleteHome = new CustomMenuForm(homesForm, Component.text("Delete Home").color(NamedTextColor.DARK_RED));
        addHome.addInput(name -> player.performCommand("sethome " + name),
                Component.text("Enter a name for your home."), "Enter name here...", "");
        deleteHome.addDropdown(select -> player.performCommand("delhome " + homes.get(select)),
                Component.text("Select a home to delete."), 0, homes.toArray(new String[0]));

        homesForm.addFormButton(addHome);
        if (!homes.isEmpty())
            homesForm.addFormButton(deleteHome);
        homes.forEach(home -> homesForm.addSimpleButton(Component.text(home), () -> player.performCommand("home " + home)));
        // Homes end

        // Settings start
        settingsForm.addSimpleButton(Component.text("Toggle PvP").color(NamedTextColor.RED), () -> player.performCommand("togglepvp"))
                .addSimpleButton(Component.text("Toggle Scoreboard").color(NamedTextColor.DARK_PURPLE),
                        () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "tab scoreboard toggle " + player.getName()));
        // Settings end

        // Main menu start
        // Add main menu form buttons
        if (hasTpRequest) {
            menuForm.addSimpleButton(Component.text("Accept Teleport").color(NamedTextColor.GREEN), () -> player.performCommand("tpaccept"))
                    .addSimpleButton(Component.text("Deny Teleport").color(NamedTextColor.RED), () -> player.performCommand("tpdeny"));
        }
        menuForm.addFormButton(playersForm)
                .addFormButton(teleportsForm)
                .addFormButton(homesForm)
                .addFormButton(settingsForm);
        // Main menu end

        return menuForm.create(floodgate.getPlayer(player.getUniqueId()));
    }
}

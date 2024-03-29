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
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.forms.CustomMenuForm;
import xyz.novaserver.mechanics.forms.SimpleMenuForm;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.List;

public class NavigationListener implements Listener {

    private final NavigationBookFeature feature;
    private final NovaMechanics plugin;

    private final FloodgateApi floodgate;
    private final Essentials essentials;

    public NavigationListener(NavigationBookFeature feature) {
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
        // Cancel dropping item if it's a book
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
            playersForm.content(Component.text("Choose a player to teleport to."));
        } else {
            playersForm.content(Component.text("There are no players online."));
        }
        playerNames.forEach(p -> playersForm.simpleButton(Component.text(p), () -> player.performCommand("tpa " + p)));
        // Players end

        // Locations start
        // Add locations to the location menu
        teleportsForm.simpleButton(Component.text("Spawn"), () -> player.performCommand("spawn"))
                .simpleButton(Component.text("Wild/Random"), () -> player.performCommand("wild"));
        // Locations end

        // Homes start
        List<String> homes = essUser.getHomes();
        CustomMenuForm addHome = new CustomMenuForm(homesForm, Component.text("Add Home").color(NamedTextColor.DARK_GREEN));
        CustomMenuForm deleteHome = new CustomMenuForm(homesForm, Component.text("Delete Home").color(NamedTextColor.DARK_RED));
        addHome.input(name -> player.performCommand("sethome " + name),
                Component.text("Enter a name for your home."), "Enter name here...", "");
        deleteHome.dropdown(select -> player.performCommand("delhome " + homes.get(select)),
                Component.text("Select a home to delete."), 0, homes.toArray(new String[0]));

        homesForm.formButton(addHome);
        if (!homes.isEmpty())
            homesForm.formButton(deleteHome);
        homes.forEach(home -> homesForm.simpleButton(Component.text(home), () -> player.performCommand("home " + home)));
        // Homes end

        // Settings start
        settingsForm.simpleButton(Component.text("Toggle PvP").color(NamedTextColor.RED), () -> player.performCommand("togglepvp"))
                .simpleButton(Component.text("Toggle Scoreboard").color(NamedTextColor.DARK_PURPLE),
                        () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "tab scoreboard toggle " + player.getName()));
        // Settings end

        // Main menu start
        // Add main menu form buttons
        if (hasTpRequest) {
            menuForm.simpleButton(Component.text("Accept Teleport").color(NamedTextColor.GREEN), () -> player.performCommand("tpaccept"))
                    .simpleButton(Component.text("Deny Teleport").color(NamedTextColor.RED), () -> player.performCommand("tpdeny"));
        }
        menuForm.formButton(playersForm)
                .formButton(teleportsForm)
                .formButton(homesForm)
                .formButton(settingsForm);
        // Main menu end

        return menuForm.create(floodgate.getPlayer(player.getUniqueId()));
    }
}

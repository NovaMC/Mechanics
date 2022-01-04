package xyz.novaserver.mechanics.features.navigation_book;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.Form;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import xyz.novaserver.mechanics.NovaMechanics;
import xyz.novaserver.mechanics.item.ItemUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NavigationListener implements Listener {

    private final NovaMechanics plugin;
    private final FloodgateApi floodgate;
    private final Essentials essentials;

    private final NavigationBook TEST_ITEM;

    public NavigationListener(NovaMechanics plugin) {
        this.plugin = plugin;
        this.floodgate = FloodgateApi.getInstance();
        this.essentials = (Essentials) this.plugin.getServer().getPluginManager().getPlugin("Essentials");
        this.TEST_ITEM = new NavigationBook(plugin);
    }

    @EventHandler
    public void onNavigationInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!floodgate.isFloodgatePlayer(player.getUniqueId())) {
            return;
        }

        FloodgatePlayer fPlayer = floodgate.getPlayer(player.getUniqueId());

        if (event.hasItem() && (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                && ItemUtils.instanceOf(event.getItem(), TEST_ITEM, plugin)) {

            event.setCancelled(true);
            fPlayer.sendForm(getMainForm(player));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        givePlayerBook(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        givePlayerBook(event.getPlayer());
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        // Cancel moving item if its a book
        if (ItemUtils.instanceOf(event.getCurrentItem(), TEST_ITEM, plugin)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        // Cancel dropping item if its a book
        if (ItemUtils.instanceOf(event.getItemDrop().getItemStack(), TEST_ITEM, plugin)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Remove book from drops
        event.getDrops().removeIf(item -> ItemUtils.instanceOf(item, TEST_ITEM, plugin));
    }

    private void givePlayerBook(Player player) {
        final int SLOT = 8;
        ItemStack slotItem = player.getInventory().getItem(SLOT);

        // Remove book if player is joining from their linked java account
        if (!floodgate.isFloodgatePlayer(player.getUniqueId())) {
//            player.getInventory().forEach(item -> {
//                if (ItemUtils.instanceOf(item, TEST_ITEM, plugin)) {
//                    player.getInventory().remove(item);
//                }
//            });
            if (ItemUtils.instanceOf(slotItem, TEST_ITEM, plugin)) {
                player.getInventory().remove(slotItem);
            }
        }
        // Give players a book if they are joining from bedrock
        else if (floodgate.isFloodgatePlayer(player.getUniqueId())) {
            // Return if player already has a book
//            if (Arrays.stream(player.getInventory().getContents())
//                    .anyMatch(item -> ItemUtils.instanceOf(item, TEST_ITEM, plugin))) {
//                return;
//            }
            if (ItemUtils.instanceOf(slotItem, TEST_ITEM, plugin)) {
                return;
            }

            // If slot is empty just put the book in the slot
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                player.getInventory().setItem(SLOT, TEST_ITEM);
            }
            // Give book and move item in hotbar slot
            else if (!ItemUtils.instanceOf(slotItem, TEST_ITEM, plugin)) {
                // Replace old item with book
                player.getInventory().setItem(SLOT, TEST_ITEM);

                // Add old item back to inventory
                HashMap<Integer, ItemStack> itemMap = player.getInventory().addItem(slotItem);

                // Try to give player old item by dropping if inventory is full
                if (!itemMap.isEmpty()) {
                    itemMap.forEach((integer, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
                }
            }
        }
    }

    private SimpleForm getMainForm(Player player) {
        User essUser = essentials.getUser(player.getUniqueId());
        boolean hasTpRequest = essUser.getNextTpaRequest(false, false, false) != null;

        // Button/form names
        final String ACCEPT_TP = ChatColor.GREEN + "Accept Teleport";
        final String DENY_TP = ChatColor.RED + "Deny Teleport";
        final String PLAYERS = ChatColor.BLUE + "Players";
        final String HOMES = ChatColor.DARK_AQUA + "Homes";
        final String TO_SPAWN = "To Spawn";
        final String TO_WILD = "To Wild";

        SimpleForm.Builder mainForm = SimpleForm.builder()
                .title(ChatColor.LIGHT_PURPLE + "Navigation Book")
                .optionalButton(ACCEPT_TP, hasTpRequest)
                .optionalButton(DENY_TP, hasTpRequest)
                .button(PLAYERS)
                .button(HOMES)
                .button(TO_SPAWN)
                .button(TO_WILD)
                .responseHandler((form, responseData) -> {
                    SimpleFormResponse response = form.parseResponse(responseData);
                    if (!response.isCorrect()) return;

                    // Button pressed and next form
                    String text = response.getClickedButton().getText();
                    Form nextForm;

                    if (text.equals(ACCEPT_TP)) {
                        player.performCommand("tpaccept");
                        return;
                    }
                    else if (text.equals(DENY_TP)) {
                        player.performCommand("tpdeny");
                        return;
                    }
                    else if (text.equals(TO_SPAWN)) {
                        player.performCommand("spawn");
                        return;
                    }
                    else if (text.equals(TO_WILD)) {
                        player.performCommand("wild");
                        return;
                    }
                    else if (text.equals(PLAYERS)) {
                        nextForm = getSimpleForm(player, essentials.getOnlinePlayers().stream().filter(p -> !p.equals(player))
                                .map(HumanEntity::getName).collect(Collectors.toList()), text, "To who?", "tpa");
                    }
                    else if (text.equals(HOMES)) {
                        nextForm = getHomeForm(player, essUser.getHomes(), HOMES);
                    }
                    else {
                        return;
                    }

                    // Send the next form to the player
                    floodgate.getPlayer(player.getUniqueId()).sendForm(nextForm);
                });

        return mainForm.build();
    }

    private SimpleForm getHomeForm(Player player, List<String> homes, String title) {
        SimpleForm.Builder builder = SimpleForm.builder();

        // Button/form names
        final String ADD_HOME = ChatColor.DARK_GREEN + "Add Home";
        final String DEL_HOME = ChatColor.RED + "Remove Home";

        // Populate form with home buttons
        builder.button(ADD_HOME).optionalButton(DEL_HOME, !homes.isEmpty());
        homes.forEach(builder::button);

        // Add response handler for home command
        builder.responseHandler((form, data) -> {
            SimpleFormResponse response = form.parseResponse(data);
            if (!response.isCorrect()) return;

            CustomForm.Builder nextBuilder = CustomForm.builder();
            String nextTitle = response.getClickedButton().getText();

            // Setup form based on button pressed
            if (nextTitle.equals(ADD_HOME)) {
                nextBuilder.input("Enter a name for your home.", "Enter name here...");
            }
            else if (nextTitle.equals(DEL_HOME)) {
                nextBuilder.dropdown("Select a home to delete.", homes.toArray(new String[0]));
            }
            else {
                player.performCommand("home " + response.getClickedButton().getText());
                return;
            }

            // Response handler for running sethome/delhome commands
            nextBuilder.responseHandler((nextForm, newData) -> {
                CustomFormResponse nextResponse = nextForm.parseResponse(newData);
                if (!nextResponse.isCorrect()) return;

                // Check if form is for adding or removing
                if (nextTitle.equals(ADD_HOME) && nextResponse.getInput(0) != null) {
                    player.performCommand("sethome "+ nextResponse.getInput(0));
                }
                else if (nextTitle.equals(DEL_HOME)) {
                    player.performCommand("delhome " + homes.get(nextResponse.getDropdown(0)));
                }

            }).title(nextTitle);

            // Send add/del home form
            floodgate.getPlayer(player.getUniqueId()).sendForm(nextBuilder.build());
        }).title(title).content("To what home?");

        return builder.build();
    }

    private SimpleForm getSimpleForm(Player player, Collection<String> items, String title, String content, String cmd) {
        SimpleForm.Builder builder = SimpleForm.builder();

        // Populate form with buttons
        items.forEach(builder::button);

        // Add response handler for commands
        builder.responseHandler((form, data) -> {
            SimpleFormResponse response = form.parseResponse(data);
            if (!response.isCorrect()) return;

            player.performCommand(cmd + " " + response.getClickedButton().getText());
        }).title(title).content(content);

        return builder.build();
    }
}

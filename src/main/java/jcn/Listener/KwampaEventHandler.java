package jcn.Listener;

import jcn.kwampaclan.Command.GuiCommand;
import jcn.kwampaclan.KwampaClan;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;

public class KwampaEventHandler implements Listener {
    private Connection connection;
    private KwampaClan plugin;

    public KwampaEventHandler(Connection connection, KwampaClan plugin) {
        this.connection = connection;
        this.plugin = plugin;
    }

    NamespacedKey namespacedKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("KwampaClan"), "KwampaClan");
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            Inventory inventory = event.getClickedInventory();


            if (inventory != null) {
                if(inventory.getType() == InventoryType.CHEST){
                    if(inventory.getSize() == 54){
                        return;
                    }
                }
                PersistentDataHolder holder = (PersistentDataHolder) inventory.getHolder();

                if (holder != null) {
                    PersistentDataContainer container = holder.getPersistentDataContainer();
                    if (container.get(namespacedKey, PersistentDataType.STRING) != null &&
                            container.get(namespacedKey, PersistentDataType.STRING).equals("KwampaClan")) {
                        event.setCancelled(true);
                    }
                    GuiCommand guiCommand = new GuiCommand(connection);
                    guiCommand.handleClanSettingsClick(player, clickedItem);
                }
            }
        }
    }
}

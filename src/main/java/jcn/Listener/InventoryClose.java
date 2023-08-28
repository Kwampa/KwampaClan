package jcn.Listener;

import jcn.kwampaclan.KwampaClan;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class InventoryClose implements Listener {
    private KwampaClan plugin;
    public InventoryClose(KwampaClan plugin){
        this.plugin = plugin;
    }
    NamespacedKey namespacedKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("KwampaClan"), "KwampaClan");
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof PersistentDataHolder) {
            PersistentDataHolder holder = (PersistentDataHolder) inventory.getHolder();
            PersistentDataContainer container = holder.getPersistentDataContainer();
            if (event.getPlayer() instanceof Player) {
                if (container.get(namespacedKey, PersistentDataType.STRING) != null &&
                        container.get(namespacedKey, PersistentDataType.STRING).equals("KwampaClan")) {
                    container.set(namespacedKey, PersistentDataType.STRING, "Default");
                }
            }
        }
    }
}

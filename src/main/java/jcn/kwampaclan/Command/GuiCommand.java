package jcn.kwampaclan.Command;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.Material.*;
import static org.bukkit.Material.PAPER;
import static org.bukkit.event.inventory.InventoryType.ANVIL;

public class GuiCommand implements Listener {
    private Connection connection;
    private Logger logger;

    public GuiCommand(Connection connection) {
        this.connection = connection;
        this.logger = Bukkit.getLogger();
    }

    public void creativeGui(Player player) {
        Inventory clanMenu = Bukkit.createInventory(player, 27, "Меню клана");
        clanMenu.setItem(10, logicNameButton(player));
        clanMenu.setItem(13, logicStatisticButton());
        clanMenu.setItem(16, logicSettingButton());
        player.openInventory(clanMenu);
    }

    public ItemStack logicNameButton(Player player) {
        ItemStack nameButton = new ItemStack(DIAMOND_BLOCK);
        ItemMeta nameButtonItemMeta = nameButton.getItemMeta();
        nameButtonItemMeta.setCustomModelData(555);
        nameButtonItemMeta.setDisplayName(ChatColor.YELLOW + "Информация");

        String clanName = "";
        String clanPrefix = "";
        String creatorName = "";
        String membersString = "";
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname, clanprefix, clancreator, members FROM clans WHERE INSTR(members, ?) > 0");
            statement.setString(1, player.getName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clanName = resultSet.getString("clanname");
                clanPrefix = resultSet.getString("clanprefix");
                creatorName = resultSet.getString("clancreator");
                membersString = resultSet.getString("members");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> membersList = Arrays.asList(membersString.split(","));

        nameButtonItemMeta.setLore(Arrays.asList(
                ChatColor.GREEN + "Название: " + ChatColor.WHITE + clanName +
                        ChatColor.GREEN + ", Префикс: " + ChatColor.WHITE + clanPrefix +
                        ChatColor.GREEN + ", Создатель: " + ChatColor.WHITE + creatorName +
                        ChatColor.GREEN + ", Участники: " + ChatColor.WHITE + String.join(", ", membersList)
        ));
        nameButton.setItemMeta(nameButtonItemMeta);
        return nameButton;
    }

    private ItemStack logicStatisticButton() {
        ItemStack statsticButton = new ItemStack(EMERALD_BLOCK);
        ItemMeta statsticButtonItemMeta = statsticButton.getItemMeta();
        statsticButtonItemMeta.setCustomModelData(555);
        statsticButtonItemMeta.setDisplayName(ChatColor.GREEN + "Участники");
        statsticButtonItemMeta.setLore(Collections.singletonList("Нажмите, чтобы посмотреть список"));
        statsticButton.setItemMeta(statsticButtonItemMeta);
        statsticButton = addIdentifier(statsticButton, "members_inventory");
        return statsticButton;
    }

    public ItemStack logicSettingButton() {
        ItemStack settingButton = new ItemStack(NETHERITE_BLOCK);
        ItemMeta settingButtonItemMeta = settingButton.getItemMeta();
        settingButtonItemMeta.setCustomModelData(555);
        settingButtonItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Настройки");
        settingButtonItemMeta.setLore(Collections.singletonList("Нажмите, чтобы открыть настройки клана"));
        settingButton.setItemMeta(settingButtonItemMeta);
        settingButton = addIdentifier(settingButton, "clan_settings");
        return settingButton;
    }

    private ItemStack addIdentifier(ItemStack item, String identifier) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(NamespacedKey.minecraft("custom-identifier"), PersistentDataType.STRING, identifier);
        item.setItemMeta(itemMeta);
        return item;
    }

    public void handleClanSettingsClick(Player player, ItemStack clickedItem) {

        if (clickedItem == null) {
            return;
        }

        if (clickedItem.getType() == AIR) {
            return;
        }

        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        String itemName = itemMeta.getDisplayName();

        switch (itemName) {
            case "§eИнформация":
                player.sendMessage("Информация, хули тыкаешь?");
                break;
            case "§aУчастники":
                openMembersInventory(player);
                break;
            case "§dНастройки":
                openSetingInventory(player);
                break;
            case "Название клана":
                if(player.hasPermission("clan.creator")) {AnvilGuiReName(player);}
                break;
            case "Префикс клана":
                if(player.hasPermission("clan.creator")) {AnvilGuiReTag(player);}
                break;
        }
    }

    public void openMembersInventory(Player player) {
        String clanName = "";
        String membersString = "";
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname, members FROM clans WHERE INSTR(members, ?) > 0");
            statement.setString(1, player.getName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clanName = resultSet.getString("clanname");
                membersString = resultSet.getString("members");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> membersList = Arrays.asList(membersString.split(","));
        Inventory membersInventory = Bukkit.createInventory(player, 27, "Участники клана " + clanName);

        for (String memberName : membersList) {
            ItemStack skull = new ItemStack(PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(memberName));
            skullMeta.setDisplayName(memberName);
            skull.setItemMeta(skullMeta);
            membersInventory.addItem(skull);
        }

        player.openInventory(membersInventory);
    }

    public void openSetingInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, "Настройки клана");
        inventory.setItem(1, book());
        inventory.setItem(7, name_tag());
        player.openInventory(inventory);
    }

    private ItemStack book() {
        ItemStack book = new ItemStack(BOOK);
        ItemMeta bookItemMeta = book.getItemMeta();
        bookItemMeta.setCustomModelData(555);
        bookItemMeta.setDisplayName("Название клана");
        bookItemMeta.setLore(Collections.singletonList("Нажмите, чтобы изменить название клана"));
        book.setItemMeta(bookItemMeta);
        book = addIdentifier(book, "book_anvil");
        return book;
    }

    private ItemStack name_tag() {
        ItemStack book = new ItemStack(NAME_TAG);
        ItemMeta bookItemMeta = book.getItemMeta();
        bookItemMeta.setCustomModelData(555);
        bookItemMeta.setDisplayName("Префикс клана");
        bookItemMeta.setLore(Collections.singletonList("Нажмите, чтобы изменить префикс клана"));
        book.setItemMeta(bookItemMeta);
        book = addIdentifier(book, "name_tag_anvil");
        return book;
    }
    public void AnvilGuiReName(Player player) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.title("Имя клана");
        builder.text("Укажите новое название клана");
        builder.plugin(Bukkit.getPluginManager().getPlugin("KwampaClan"));
        builder.onClick((slot, stateSnapshot) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }
            if (!stateSnapshot.getText().equals("")) {
                String newName = stateSnapshot.getText();
                String name = ChatColor.translateAlternateColorCodes('&', newName);
                player.sendMessage("Новое название клана: " + name);
                updateClanNameByCreator(player.getName(), name);
                return Arrays.asList(AnvilGUI.ResponseAction.close());
            } else {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Попробуйте еще раз"));
            }
        });
        builder.open(player);
    }

    public void AnvilGuiReTag(Player player) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.title("Префикс клана");
        builder.text("Укажите новый префикс клана");
        builder.plugin(Bukkit.getPluginManager().getPlugin("KwampaClan"));
        builder.onClick((slot, stateSnapshot) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }
            if (!stateSnapshot.getText().equals("")) {
                String newName = stateSnapshot.getText();
                String prefix = ChatColor.translateAlternateColorCodes('&', newName);
                if(lengthWithoutColor(prefix) == 2) {
                    player.sendMessage("Новый префикс клана: " + prefix);
                    updateClanPrefixByCreator(player.getName(), prefix);
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                }
                else {
                    player.sendMessage("Префикс должен состоять из 2х символов");
                }
            } else {
                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Попробуйте еще раз"));
            }
            return null;
        });
        builder.open(player);
    }

    public void updateClanPrefixByCreator(String creatorName, String newPrefix) {
        try {PreparedStatement statement = connection.prepareStatement("UPDATE clans SET clanprefix = ? WHERE clancreator = ?");
            statement.setString(1, newPrefix);
            statement.setString(2, creatorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClanNameByCreator(String creatorName, String newName) {
        try {PreparedStatement statement = connection.prepareStatement("UPDATE clans SET clanname = ? WHERE clancreator = ?");
            statement.setString(1, newName);
            statement.setString(2, creatorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     Counts the number of characters in a string, ignoring Minecraft color codes.
     @param s String in which you need to count the characters without color codes
     @return Number of characters in line without color characters
     @author ClaimGaming
     */
    public int lengthWithoutColor(String s){
        char[] chars = s.toCharArray();

        List<Character> color_codes = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
        int count = 0;

        for(int i = 0; i < chars.length; i++){
            if(chars[i] == '§' && i != chars.length - 1){
                if(color_codes.contains(chars[i+1])){
                    count--;
                }
            }else{
                count++;
            }
        }
        return count;
    }
}

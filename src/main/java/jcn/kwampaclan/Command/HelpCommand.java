package jcn.kwampaclan.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    public void HelpCommand(Player player){
        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + " Спиоск всех комманд плагина KwampaClan!");
        player.sendMessage(ChatColor.GOLD  + "---------------------------------------");
        player.sendMessage(ChatColor.RESET + "/clan create - Комманда для создания клана.");
        player.sendMessage(ChatColor.RESET + "/clan invite - Комманда для приглашения игрока в клан.");
        player.sendMessage(ChatColor.RESET + "/clan accept - Комманда для принятия приглашения в клан.");
        player.sendMessage(ChatColor.RESET + "/clan leave - Коммадна для выхода из клана.");
        player.sendMessage(ChatColor.RESET + "/clan kick - Команда для удаления игрока из клана.");
        player.sendMessage(ChatColor.RESET + "/clan gui - Меню клана.");
        player.sendMessage(ChatColor.RESET + "/clan list - Список всех кланнов.");
        player.sendMessage(ChatColor.RESET + "/clan delete - Удаление своего кланна.");
        player.sendMessage(ChatColor.GOLD  + "---------------------------------------");
    }
}

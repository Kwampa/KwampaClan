package jcn.kwampaclan.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand {
    public void HelpCommand(Player player){
        player.sendMessage(ChatColor.BLUE + "Спиоск всех комманд плагина KwampaClan!");
        player.sendMessage(ChatColor.BLUE + "---------------------------------------");
        player.sendMessage(ChatColor.BLUE + "/clan create - Комманда для создания клана.");
        player.sendMessage(ChatColor.BLUE + "/clan invite - Комманда для приглашения игрока в клан.");
        player.sendMessage(ChatColor.BLUE + "/clan accept - Комманда для принятия приглашения в клан.");
        player.sendMessage(ChatColor.BLUE + "/clan leave - Коммадна для выхода из клана.");
        player.sendMessage(ChatColor.BLUE + "/clan kick - Команда для удаления игрока из клана.");
        player.sendMessage(ChatColor.BLUE + "/clan gui - Меню клана.");
    }
}

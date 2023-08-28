package jcn.kwampaclan.Command;

import jcn.kwampaclan.DataBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public class ClanListCommand {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;

    public ClanListCommand(Connection connection){
        this.connection = connection;
    }

    public void  ClanList(Player player) throws SQLException {
        DataBase dataBase = new DataBase(connection);
        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Список всех кланнов");
        player.sendMessage(ChatColor.GOLD + "-------------------");
        player.sendMessage(dataBase.getClanInfo());
        player.sendMessage(ChatColor.GOLD + "-------------------");
    }
}

package jcn.kwampaclan.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanListCommand {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;

    public ClanListCommand(Connection connection){
        this.connection = connection;
    }

    public void  ClanList(Player player) throws SQLException {
        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Список всех кланнов");
        player.sendMessage(ChatColor.GOLD + "-------------------");
        PreparedStatement statement = connection.prepareStatement("SELECT clanname AS ClanName, members AS Members, clanprefix AS Prefix " + "FROM clans;");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            String clanName = resultSet.getString("ClanName");
            String members = resultSet.getString("Members");
            String prefix = resultSet.getString("Prefix");
            int ValueOfMembers = members.split(",").length;
            player.sendMessage(ChatColor.RESET + "Название: " +  clanName);
            player.sendMessage(ChatColor.RESET + "Префикс: " + prefix);
            player.sendMessage(ChatColor.RESET + "Количетсво участников: " + ValueOfMembers);
            player.sendMessage(" ");
        }
        player.sendMessage(ChatColor.GOLD + "-------------------");
    }
}

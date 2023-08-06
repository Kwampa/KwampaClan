package jcn.kwampaclan.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanListCommand {
    private Connection connection;

    public ClanListCommand(Connection connection){
        this.connection = connection;
    }

    public void  ClanList(Player player) throws SQLException {
        player.sendMessage(ChatColor.BLUE + "Список всех кланнов");
        player.sendMessage(ChatColor.BLUE + "-------------------");
        PreparedStatement statement = connection.prepareStatement("SELECT clanname AS ClanName, members AS Members, clanprefix AS Prefix " + "FROM clans;");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){
            String clanName = resultSet.getString("ClanName");
            String members = resultSet.getString("Members");
            String prefix = resultSet.getString("Prefix");
            int ValueOfMembers = members.split(",").length;
            player.sendMessage("Название: " +  clanName + ChatColor.RESET + " Префикс: " + prefix);
            player.sendMessage("Количетсво участников: " + ChatColor.GOLD + ValueOfMembers);
        }
        player.sendMessage(ChatColor.BLUE + "-------------------");
    }
}

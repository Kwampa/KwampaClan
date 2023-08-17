package jcn.kwampaclan.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KickCommand {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;

    public KickCommand(Connection connection) {
        this.connection = connection;
    }

    public void execute(Player player, String[] strings) {
        String targetPlayerName = strings[1];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED  + " Игрок с именем " + targetPlayerName + " не найден или не в сети.");
            return;
        }

        String executorClanName = getClanName(player);
        String targetClanName = getClanName(targetPlayer);

        if (executorClanName == null || targetClanName == null || !executorClanName.equals(targetClanName)) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED  + "Игрок с именем " + targetPlayerName + " не является участником вашего клана.");
            return;
        }

        String executorClanCreator = getClanCreator(player);
        if (executorClanCreator == null || !executorClanCreator.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Вы не являетесь главой клана и не можете кикать участников.");
            return;
        }

        removePlayerFromClan(targetPlayer);

        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Игрок с именем" + targetPlayerName + " успешно кикнут из клана.");
    }

    private String getClanName(Player player) {
        String playerName = player.getName();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname FROM clans WHERE members LIKE ?");
            statement.setString(1, "%" + playerName + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String clanName = resultSet.getString("clanname");
                resultSet.close();
                statement.close();
                return clanName;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getClanCreator(Player player) {
        String playerName = player.getName();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clancreator FROM clans WHERE members LIKE ?");
            statement.setString(1, "%" + playerName + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String clanCreator = resultSet.getString("clancreator");
                resultSet.close();
                statement.close();
                return clanCreator;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void removePlayerFromClan(Player player) {
        String playerName = player.getName();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET members = REPLACE(members, ?, '') WHERE members LIKE ?");
            statement.setString(1, "," + playerName);
            statement.setString(2, "%" + playerName + "%");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
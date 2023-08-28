package jcn.kwampaclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase {
    private final Connection connection;
    private Logger logger = Bukkit.getLogger();

    public DataBase(Connection connection) {
        this.connection = connection;
    }

    public String getClanInfo() {
        StringBuilder info = new StringBuilder();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname AS ClanName, members AS Members, clanprefix AS Prefix FROM clans;");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String clanName = resultSet.getString("ClanName");
                String members = resultSet.getString("Members");
                String prefix = resultSet.getString("Prefix");
                int valueOfMembers = members.split(",").length;

                info.append(ChatColor.RESET).append("Название: ").append(clanName).append("\n");
                info.append(ChatColor.RESET).append("Префикс: ").append(prefix).append("\n");
                info.append(ChatColor.RESET).append("Количество участников: ").append(valueOfMembers).append("\n");
                info.append("\n");
            }
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while fetching clan info from the database.", e);
        }
        return info.toString();
    }

    public void deleteClan(Player player) throws SQLException {
        String clanName = getClanName(player);
        if (clanName == null) {
            logger.warning("Clan name not found for player: " + player.getName());
            return;
        }

        logger.info("Deleting clan: " + clanName);
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM clans WHERE clanname = ?")) {
            preparedStatement.setString(1, clanName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while deleting clan from the database.", e);
        }
    }

    public void updateMembersList(Player inviter, String playerName) {
        try {
            logger.info("Updating clan member list after adding a new player.");
            String clanname = getClanName(inviter);
            if (clanname == null) {
                logger.warning("Clan name not found for player: " + inviter.getName());
                return;
            }

            String existingMembers = getClanMembers(clanname);
            existingMembers = existingMembers.isEmpty() ? playerName : existingMembers + "," + playerName;

            try (PreparedStatement statement = connection.prepareStatement("UPDATE clans SET members = ? WHERE clanname = ?")) {
                statement.setString(1, existingMembers);
                statement.setString(2, clanname);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while updating clan member list.", e);
        }
    }

    private String getClanMembers(String clanname) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT members FROM clans WHERE clanname = ?")) {
            statement.setString(1, clanname);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("members") : "";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while fetching clan members from the database.", e);
        }
        return "";
    }

    public String getClanName(Player player) {
        String playerName = player.getName();
        try (PreparedStatement statement = connection.prepareStatement("SELECT clanname FROM clans WHERE members LIKE ?")) {
            statement.setString(1, "%" + playerName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("clanname");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while fetching clan name from the database.", e);
        }
        return null;
    }

    public void removePlayerFromClan(Player player) {
        String playerName = player.getName();
        try {
            logger.info("Removing player from clan: " + playerName);
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET members = REPLACE(members, ?, '') WHERE members LIKE ?");
            statement.setString(1, "," + playerName);
            statement.setString(2, "%" + playerName + "%");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while removing player from the clan.", e);
        }
    }

    public void updateClanNameByCreator(String creatorName, String newName) {
        try {
            logger.info("Updating clan name for creator: " + creatorName);
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET clanname = ? WHERE clancreator = ?");
            statement.setString(1, newName);
            statement.setString(2, creatorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while updating clan name by creator.", e);
        }
    }

    public void updateClanPrefixByCreator(String creatorName, String newPrefix) {
        try {
            logger.info("Updating clan prefix for creator: " + creatorName);
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET clanprefix = ? WHERE clancreator = ?");
            statement.setString(1, newPrefix);
            statement.setString(2, creatorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while updating clan prefix by creator.", e);
        }
    }
    public String getClanCreator(Player player) {
        String playerName = player.getName();
        try (PreparedStatement statement = connection.prepareStatement("SELECT clancreator FROM clans WHERE members LIKE ?")) {
            statement.setString(1, "%" + playerName + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("clancreator");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while fetching clan creator from the database.", e);
        }
        return null;
    }
}

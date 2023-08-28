package jcn.kwampaclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DataBase {
    private Connection connection;
    public DataBase(Connection connection){
        this.connection = connection;
    }
    public String getClanInfo() {//Получаем информацию по всем кланам из базы данных
        StringBuilder info = new StringBuilder();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname AS ClanName, members AS Members, clanprefix AS Prefix " + "FROM clans;");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                String clanName = resultSet.getString("ClanName");
                String members = resultSet.getString("Members");
                String prefix = resultSet.getString("Prefix");
                int valueOfMembers = members.split(",").length;
                info.append(ChatColor.RESET + "Название: ").append(clanName).append("\n");
                info.append(ChatColor.RESET + "Префикс: ").append(prefix).append("\n");
                info.append(ChatColor.RESET + "Количество участников: ").append(valueOfMembers).append("\n");
                info.append("\n");
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info.toString();
    }

    public void deleteClan(Player player) throws SQLException {//Удаление клана из базы данных
        Logger logger = Bukkit.getLogger();
        String clanname = getClanName(player);
        logger.info("Удаляем клан");
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM clans WHERE clanname = ?");
        preparedStatement.setString(1, clanname);
        preparedStatement.executeUpdate();
    }

    public void updateMembersList(Player inviter, String playerName) {//Обновление списка участников клана после добавления нового игрока
        try {
            String clanname = getClanName(inviter);
            String existingMembers = getClanMembers(clanname);
            existingMembers = existingMembers.isEmpty() ? playerName : existingMembers + "," + playerName;
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET members = ? WHERE clanname = ?");
            statement.setString(1, existingMembers);
            statement.setString(2, clanname);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getClanMembers(String clanname) throws SQLException {//Получаем список игроков из определнного клана
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT members FROM clans WHERE clanname = ?");
            statement.setString(1, clanname);
            ResultSet resultSet = statement.executeQuery();
            String members = resultSet.next() ? resultSet.getString("members") : "";
            resultSet.close();
            statement.close();
            return members;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getClanName(Player player) {//Получаем название кланна по нику участника клана
        String playerName = player.getName();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname FROM clans WHERE members LIKE ?");
            statement.setString(1, "%" + playerName + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String clanName = resultSet.next() ? resultSet.getString("clanname") : "";
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

    public String getClanCreator(Player player) {//Получаем создателя клана по названию клана
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

    public void removePlayerFromClan(Player player) {//Удаляем игрока из клана
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

    public void updateClanNameByCreator(String creatorName, String newName) {//Обновляем название клана
        try {PreparedStatement statement = connection.prepareStatement("UPDATE clans SET clanname = ? WHERE clancreator = ?");
            statement.setString(1, newName);
            statement.setString(2, creatorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClanPrefixByCreator(String creatorName, String newPrefix) {//Обновляем префикс клана
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET clanprefix = ? WHERE clancreator = ?");
            statement.setString(1, newPrefix);
            statement.setString(2, creatorName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

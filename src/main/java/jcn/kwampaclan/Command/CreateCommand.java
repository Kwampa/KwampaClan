package jcn.kwampaclan.Command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;

public class CreateCommand {
    private final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;
    private LuckPerms luckPerms;
    private Logger logger;

    public CreateCommand(Connection connection, LuckPerms luckPerms) {
        this.connection = connection;
        this.luckPerms = luckPerms;
        this.logger = Bukkit.getLogger();
    }

    public void ClanCreate(Player player, String[] strings) throws SQLException {
        if (strings.length < 3) {
            player.damage(1);
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Ошибка! Название клана должно состоять из 2х слов!");
            return;
        }

        if (!player.hasPermission("clan.creator")) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Ошибка! У вас недостаточно прав для создания кланов. Обратитесь в тикет, если хотите создать клан.");
            return;
        }

        String clanname = strings[1] + " " + strings[2];
        logger.info("Попытка создания нового клана с названием: " + clanname);

        if (checkClanName(clanname)) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Ошибка! Клан с таким названием уже существует.");
            return;
        }

        if (player.hasPermission("clan.member")) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Ошибка! Вы уже находитесь в клане.");
            return;
        }

        String clanPrefix = createClanPrefix(strings);
        String id = createRandomID();
        boolean available = checkClanID(id);

        if (!available) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Ошибка! Не удалось создать клан. Повторите попытку позже.");
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO clans (clancreator, idclan, clanname, clanprefix, members) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, player.getName());
            statement.setString(2, id);
            statement.setString(3, clanname);
            statement.setString(4, clanPrefix);

            // Получаем существующих участников клана (включая создателя)
            String existingMembers = getExistingMembers(clanname);
            existingMembers = existingMembers.isEmpty() ? player.getName() : existingMembers + "," + player.getName();
            statement.setString(5, existingMembers);

            statement.executeUpdate();
            statement.close();

            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Клан успешно создан!");
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Название клана: " + clanname);
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Префикс клана: " + "[" + clanPrefix + "]");
            addPerm(player, "clan.member");
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Произошла ошибка при создании клана. Повторите попытку позже.");
        }
    }

    public boolean checkClanName(String clanname) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM clans WHERE clanname = ?");
            statement.setString(1, clanname);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            statement.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String createClanPrefix(String[] strings){
        char char1 = strings[1].charAt(0);
        char char2 = strings[2].charAt(0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(char1);
        stringBuilder.append(char2);
        return stringBuilder.toString();
    }

    public String createRandomID(){
        StringBuilder result = new StringBuilder(10);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            result.append(randomChar);
        }
        return result.toString();
    }

    public boolean checkClanID(String id){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM clans WHERE idclan = ?");
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            statement.close();
            return !exists;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getExistingMembers(String clanname) throws SQLException {
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

    public void addPerm(Player player, String permission) {
        User user = this.luckPerms.getUserManager().getUser(player.getUniqueId());
        if (permission.equals("clan.member")) {
            user.data().add(Node.builder(permission).build());
        }
        this.luckPerms.getUserManager().saveUser(user);
    }
}
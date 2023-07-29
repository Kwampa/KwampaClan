package jcn.kwampaclan.Command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class InviteAcceptCommand {
    private final Map<Player, Player> time;
    private final Map<Player, Long> inviteTimes; // Карта для хранения времени приглашения
    private Connection connection;
    private LuckPerms luckPerms;
    private Logger logger;

    public InviteAcceptCommand(Connection connection, LuckPerms luckPerms, Map<Player, Player> time) {
        this.connection = connection;
        this.luckPerms = luckPerms;
        this.logger = Bukkit.getLogger();
        this.time = time;
        this.inviteTimes = new HashMap<>();
    }

    public boolean createInvite(Player player, String[] strings) {
        String targetPlayerName = strings[1];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage("Игрок с именем " + targetPlayerName + " не найден или не в сети.");
            return true;
        }

        if (targetPlayer.hasPermission("clan.perm")) {
            player.sendMessage(targetPlayerName + " уже состоит в другой команде.");
            return true;
        }

        if (isPlayerInvited(targetPlayer)) {
            player.sendMessage("Игрок " + targetPlayerName + " уже приглашен в клан.");
            return true;
        }

        sendInvitation(player, targetPlayer);
        return false;
    }

    public void sendInvitation(Player player, Player targetPlayer) {
        time.put(targetPlayer, player);
        targetPlayer.sendMessage("Вы получили приглашение вступить в клан от игрока: " + player.getName());
        targetPlayer.sendMessage("Принять приглашение: /clan accept");

        // Устанавливаем временную метку приглашения
        inviteTimes.put(targetPlayer, System.currentTimeMillis());
    }

    public boolean isPlayerInvited(Player player) {
        return time.containsKey(player);
    }

    public void acceptInvitation(Player player, LuckPerms luckPerms) {
        Player inviter = time.remove(player);
        if (inviter != null) {
            player.sendMessage("Вы приняли приглашение от игрока " + inviter.getName());

            // Выдаем разрешение игроку при принятии приглашения
            addPerm(player, "clan.member");

            // Добавляем игрока в список участников клана
            updateMembersList(inviter, player.getName());

            // Уведомляем отправителя о том, что приглашение принято
            inviter.sendMessage("Игрок " + player.getName() + " принял ваше приглашение в клан.");

            // Проверяем и удаляем истекшие приглашения
            checkExpiredInvitations();
        }
    }

    public void addPerm(Player player, String permission) {
        User user = this.luckPerms.getUserManager().getUser(player.getUniqueId());
        if (permission.equals("clan.member")) {
            user.data().add(Node.builder(permission).build());
        }
        this.luckPerms.getUserManager().saveUser(user);
    }

    private void updateMembersList(Player inviter, String playerName) {
        try {
            String clanname = getClanNameByCreator(inviter.getName());
            if (clanname.isEmpty()) {
                logger.warning("Не удалось найти клан для игрока " + inviter.getName());
                return;
            }

            String existingMembers = getExistingMembers(clanname);
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

    private String getClanNameByCreator(String creatorName) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT clanname FROM clans WHERE clancreator = ?");
            statement.setString(1, creatorName);
            ResultSet resultSet = statement.executeQuery();
            String clanName = resultSet.next() ? resultSet.getString("clanname") : "";
            resultSet.close();
            statement.close();
            return clanName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getExistingMembers(String clanname) throws SQLException {
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

    // Метод для проверки и удаления истекших приглашений (старше 30 секунд)
    public void checkExpiredInvitations() {
        long currentTime = System.currentTimeMillis();
        inviteTimes.entrySet().removeIf(entry -> currentTime - entry.getValue() >= 30000);
    }
}
package jcn.kwampaclan.Command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LeaveCommand {
    private Connection connection;
    private LuckPerms luckPerms;

    public LeaveCommand(Connection connection, LuckPerms luckPerms) {
        this.connection = connection;
        this.luckPerms = luckPerms;
    }

    public void leave(Player player) {
        if (!player.hasPermission("clan.creator")) {
            if (player.hasPermission("clan.member")) {
                player.sendMessage("Вы покинули клан");

                // Забираем у игрока пермишен "clan.member"
                removePerm(player, "clan.member");
                removePlayerFromClan(player);

            } else {
                player.sendMessage("Вы не находитесь в клане");
            }
        } else {
            player.sendMessage("Вы не можете покинуть свой клан");
            player.sendMessage("В крайнем случае свяжитесь с администрацией");
        }
    }

    public void removePerm(Player player, String permission) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (permission.equals("clan.member")) {
            user.data().remove(Node.builder(permission).build());
        }
        luckPerms.getUserManager().saveUser(user);
    }
    private void removePlayerFromClan(Player player) {
        String playerName = player.getName();
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE clans SET members = REPLACE(members, ?, '') WHERE members LIKE ?");
            statement.setString(1,  "," + playerName);
            statement.setString(2, "%" + playerName + "%");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
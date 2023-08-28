package jcn.kwampaclan.Command;

import jcn.kwampaclan.DataBase;
import jcn.kwampaclan.LuckpPerms;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class InviteAcceptCommand {
    private final Map<Player, Player> time;
    private final Map<Player, Long> inviteTimes;
    private Connection connection;
    private LuckPerms luckPerms;
    private Logger logger;
    public static final String PLUGINPREFIX = "[KwampaClan]";

    public InviteAcceptCommand(Connection connection, LuckPerms luckPerms, Map<Player, Player> time) {
        this.connection = connection;
        this.luckPerms = luckPerms;
        this.logger = Bukkit.getLogger();
        this.time = time;
        this.inviteTimes = new HashMap<>();
    }

    public boolean createInvite(Player player, String[] strings) {
        String inviterName = strings[1];
        Player inviter = Bukkit.getPlayer(inviterName);

        if (inviter == null || !inviter.isOnline()) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Игрок с именем " + inviterName + " не найден или не в сети.");
            return true;
        }

        String targetPlayerName = strings[1];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Игрок с именем " + targetPlayerName + " не найден или не в сети.");
            return true;
        }

        if (targetPlayer.hasPermission("clan.member")) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Игрок с именем" + targetPlayerName + " уже состоит в другой команде.");
            return true;
        }

        if (isPlayerInvited(targetPlayer)) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED +  " Игрок с именем " + targetPlayerName + " уже приглашен в клан.");
            return true;
        }

        sendInvitation(player, targetPlayer);
        return false;
    }

    public void sendInvitation(Player player, Player targetPlayer) {
        time.put(targetPlayer, player);
        player.sendMessage((ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Вы отправили приглашение вступить в клан игроку: " + targetPlayer.getName()));
        targetPlayer.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Вы получили приглашение вступить в клан от игрока: " + player.getName());
        targetPlayer.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Принять приглашение: /clan accept");

        inviteTimes.put(targetPlayer, System.currentTimeMillis());
    }

    public boolean isPlayerInvited(Player player) {
        return time.containsKey(player);
    }

    public void acceptInvitation(Player player, LuckPerms luckPerms) {
        Player inviter = time.remove(player);
        if (inviter != null) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Вы приняли приглашение от игрока " + inviter.getName());

            LuckpPerms luckpPermsclass = new LuckpPerms(luckPerms);

            luckpPermsclass.addPerm(player, "clan.member");

            DataBase dataBase = new DataBase(connection);
            dataBase.updateMembersList(inviter, player.getName());

            inviter.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Игрок " + player.getName() + " принял ваше приглашение в клан.");

            checkExpiredInvitations();
        }
    }

    public void checkExpiredInvitations() {
        long currentTime = System.currentTimeMillis();
        inviteTimes.entrySet().removeIf(entry -> currentTime - entry.getValue() >= 30000);
    }
}
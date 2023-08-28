package jcn.kwampaclan.Command;

import jcn.kwampaclan.DataBase;
import jcn.kwampaclan.LuckpPerms;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class KickCommand {
    private LuckPerms luckPerms;
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;

    public KickCommand(Connection connection, LuckPerms luckPerms) {
        this.connection = connection;
        this.luckPerms = luckPerms;
    }

    public void execute(Player player, String[] strings) {
        String targetPlayerName = strings[1];
        Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED  + " Игрок с именем " + targetPlayerName + " не найден или не в сети.");
            return;
        }
        DataBase dataBase = new DataBase(connection);
        String executorClanName = dataBase.getClanName(player);
        String targetClanName = dataBase.getClanName(targetPlayer);

        if (executorClanName == null || targetClanName == null || !executorClanName.equals(targetClanName)) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED  + "Игрок с именем " + targetPlayerName + " не является участником вашего клана.");
            return;
        }

        String executorClanCreator = dataBase.getClanCreator(player);
        if (executorClanCreator == null || !executorClanCreator.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Вы не являетесь главой клана и не можете кикать участников.");
            return;
        }

        LuckpPerms luckpPermsclass = new LuckpPerms(luckPerms);
        luckpPermsclass.removePerm(player, "clan.member");

        dataBase.removePlayerFromClan(targetPlayer);

        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Игрок с именем " + targetPlayerName + " успешно кикнут из клана.");
    }
}
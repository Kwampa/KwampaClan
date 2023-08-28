package jcn.kwampaclan.Command;

import jcn.kwampaclan.DataBase;
import jcn.kwampaclan.LuckpPerms;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class LeaveCommand {
    private Connection connection;
    private LuckPerms luckPerms;
    public static final String PLUGINPREFIX = "[KwampaClan]";

    public LeaveCommand(Connection connection, LuckPerms luckPerms) {
        this.connection = connection;
        this.luckPerms = luckPerms;
    }

    public void leave(Player player) {
        if (!player.hasPermission("clan.creator")) {
            if (player.hasPermission("clan.member")) {
                player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Вы покинули клан");

                LuckpPerms luckpPermsclass = new LuckpPerms(luckPerms);
                luckpPermsclass.removePerm(player, "clan.member");
                DataBase dataBase = new DataBase(connection);
                dataBase.removePlayerFromClan(player);

            } else {
                player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + " Вы не находитесь в клане");
            }
        } else {
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RED + "Вы не можете покинуть свой клан");
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + "Используйте /clan delete");
        }
    }
}
package jcn.kwampaclan.ClanDeleteLogic;

import jcn.kwampaclan.Command.DeleteCommand;
import jcn.kwampaclan.DataBase;
import jcn.kwampaclan.LuckpPerms;
import net.luckperms.api.LuckPerms;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public class CommandDelete implements CommandExecutor {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;
    private LuckpPerms luckpPerms;

    public CommandDelete(Connection connection, LuckpPerms luckpPerms) {
        this.connection = connection;
        this.luckpPerms = luckpPerms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DataBase dataBase = new DataBase(connection);
            try {
                dataBase.deleteClan(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            LuckpPerms luckpPerms1 = new LuckpPerms((LuckPerms) luckpPerms);
            luckpPerms1.removePerm(player, "clan.creator");
            luckpPerms1.removePerm(player, "clan.member");
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Клан удален");
        }
        return false;
    }
}
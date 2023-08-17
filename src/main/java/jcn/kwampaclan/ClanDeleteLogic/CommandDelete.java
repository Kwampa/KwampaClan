package jcn.kwampaclan.ClanDeleteLogic;

import jcn.kwampaclan.Command.DeleteCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class CommandDelete implements CommandExecutor {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;

    public CommandDelete(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DeleteCommand deleteCommand = new DeleteCommand(connection);
            try {
                deleteCommand.deleteClanFromDB(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Клан удален");
        }
        return false;
    }
}
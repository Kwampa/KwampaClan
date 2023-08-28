package jcn.kwampaclan.ClanDeleteLogic;

import jcn.kwampaclan.Command.DeleteCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class CommandCancelDelete implements CommandExecutor {
    private Connection connection;

    public CommandCancelDelete(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DeleteCommand deleteCommand = new DeleteCommand(connection);
            deleteCommand.handleCancelDelete(player);
        }
        return true;
    }
}

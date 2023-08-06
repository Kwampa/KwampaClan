package jcn.kwampaclan;

import jcn.kwampaclan.Command.*;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class MainCommandClan implements CommandExecutor {
    private Connection connection;
    private LuckPerms luckPerms;
    private CreateCommand createCommand;
    private Map<Player, Player> time;

    public MainCommandClan(Connection connection, LuckPerms luckPerms, CreateCommand createCommand, Map<Player, Player> time, GuiCommand guiCommand, LeaveCommand leaveCommand) {
        this.connection = connection;
        this.luckPerms = luckPerms;
        this.createCommand = createCommand;
        this.time = time;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if(strings.length < 1){
            player.sendMessage("Неизвестная команнда! Для просмотра всех комманд используйте: /clan help!");
        }
        else {
        switch (strings[0]) {
            case "create":
                if (strings.length < 3) {
                    player.damage(1);
                    player.sendMessage("Название клана должно состоять из 2х слов!");
                    return true;
                }
                CreateCommand clanMethod = new CreateCommand(connection, luckPerms);
                try {
                    clanMethod.ClanCreate(player, strings);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "gui":
                if (strings.length < 1) {
                    player.sendMessage("Использование: /clan gui");
                    return true;
                }
                GuiCommand guiCommand = new GuiCommand(connection);
                guiCommand.creativeGui(player);
                break;

            case "invite":
                if (strings.length < 2) {
                    player.sendMessage("Использование: /clan invite (ник игрока)");
                    return true;
                }
                InviteAcceptCommand inviteAcceptCommand = new InviteAcceptCommand(connection, luckPerms, time);
                inviteAcceptCommand.createInvite(player, strings);
                break;

            case "accept":
                if (strings.length < 2) {
                    player.sendMessage("Использование: /clan accept (ник игрока)");
                    return true;
                }
                String inviterName = strings[1];
                Player inviter = Bukkit.getPlayer(inviterName);

                if (inviter == null || !inviter.isOnline()) {
                    player.sendMessage("Игрок с именем " + inviterName + " не найден или не в сети.");
                    return true;
                }

                inviteAcceptCommand = new InviteAcceptCommand(connection, luckPerms, time);
                inviteAcceptCommand.acceptInvitation(player, luckPerms);

                break;

            case "kick":
                if (strings.length < 2) {
                    player.sendMessage("Использование: /clan kick (ник игрока)");
                    return true;
                }
                KickCommand kickCommand = new KickCommand(connection);
                kickCommand.execute(player, strings);
                break;

            case "leave":
                LeaveCommand leaveCommand = new LeaveCommand(connection, luckPerms);
                leaveCommand.leave(player);
                break;
            case "help":
                HelpCommand helpCommand = new HelpCommand();
                helpCommand.HelpCommand(player);
                break;
            case "list":
                ClanListCommand clanListCommand = new ClanListCommand(connection);
                try {
                    clanListCommand.ClanList(player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "Неизвестная команнда! Для просмотра всех комманд используйте: /clan help!");
            }
        }
        return false;
    }
}
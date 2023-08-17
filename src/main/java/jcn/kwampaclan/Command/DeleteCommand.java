package jcn.kwampaclan.Command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DeleteCommand {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;
    public DeleteCommand(Connection connection){
        this.connection = connection;
    }

    public void DeleteClan(Player player) throws SQLException {
        if(player.hasPermission("clan.creator")){
            String clanname = getClanFromDB(player);
            Component confirmButton = Component.text("Удалить").clickEvent(ClickEvent.runCommand("/delete")).color(TextColor.color(255, 0, 0));
            Component cancelButton = Component.text("Оставить").clickEvent(ClickEvent.runCommand("/canceldelete")).color(TextColor.color(0, 204, 0));
            Component message = Component.text()
                    .append(Component.text("Вы хотите удалить клан: ").color(TextColor.color(255, 170, 0)))
                    .append(Component.text(clanname).color(TextColor.color(255, 170, 0)))
                    .append(Component.text("?").color(TextColor.color(255, 170, 0)))
                    .append(Component.newline())
                    .append(confirmButton)
                    .append(Component.text(" "))
                    .append(cancelButton)
                    .build();
            Audience audience = (Audience) player;
            ((Audience) player).sendMessage(message);
        }
    }

    public String getClanFromDB(Player player) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT clanname FROM clans WHERE clancreator LIKE ?");
        preparedStatement.setString(1, player.getName());
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {return resultSet.getString("clanname");}
        else {return null;}
    }

    public void handleCancelDelete(Player player) {
        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + "Вы решили оставить клан без изменений.");
    }

    public void deleteClanFromDB(Player player) throws SQLException {
        Logger logger = Bukkit.getLogger();
        String clanname = getClanFromDB(player);
        logger.info("Удаляем клан");
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM clans WHERE clanname = ?");
        preparedStatement.setString(1, clanname);
        preparedStatement.executeUpdate();
    }
}

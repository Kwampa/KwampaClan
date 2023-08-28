package jcn.kwampaclan.Command;

import jcn.kwampaclan.DataBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public class DeleteCommand {
    public static final String PLUGINPREFIX = "[KwampaClan]";
    private Connection connection;
    public DeleteCommand(Connection connection){
        this.connection = connection;
    }

    public void DeleteClan(Player player) throws SQLException {
        if(player.hasPermission("clan.creator")){
            DataBase dataBase = new DataBase(connection);
            String clanname = dataBase.getClanName(player);
            Component confirmButton = Component.text("Удалить").clickEvent(ClickEvent.runCommand("/delete")).color(TextColor.color(255, 0, 0));
            Component cancelButton = Component.text("Оставить").clickEvent(ClickEvent.runCommand("/canceldelete")).color(TextColor.color(0, 204, 0));
            Component message = Component.text()
                    .append(Component.text("Вы хотите удалить клан?").color(TextColor.color(255, 170, 0)))
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

    public void handleCancelDelete(Player player) {
        player.sendMessage(ChatColor.GOLD + PLUGINPREFIX + ChatColor.RESET + " Вы решили оставить клан без изменений.");
    }
}

package jcn.kwampaclan;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Placeholder extends PlaceholderExpansion {
    private KwampaClan plugin;
    private MySQLDatabaseManager databaseManager;

    public Placeholder(KwampaClan plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getMySQLDatabaseManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clan";
    }

    @Override
    public @NotNull String getAuthor() {
        return "The_JCN";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("prefix")) {
            String playerName = player.getName();
            String clanPrefix = String.valueOf(getClanPrefuxByMember(playerName, databaseManager.getConnection()));
            if (!clanPrefix.isEmpty()) {
                String coloredPrefix = "&r[" + clanPrefix.toUpperCase() + "&r]";
                return coloredPrefix;
            } else {
                return "";
            }
        }
        return null;
    }


    private String getClanPrefuxByMember(String player, Connection connection) {
        String sql = "SELECT clanprefix FROM clans WHERE FIND_IN_SET(?, members) > 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("clanprefix");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
package com.isnakebuzz.survivalgames.Database;

import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Player.GamePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDB {

    private Main plugin;

    public PlayerDB(Main plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(Player p) throws SQLException {
        if (!playerExist(p)) {
            GamePlayer gamePlayer = new GamePlayer(p, 0, 0, 0, 0, 0, 0);
            plugin.getPlayerManager().addPlayer(p, gamePlayer);
            MySQL.update("INSERT INTO SnakeSG (UUID, Wins, Kills, Deaths, Played, ArrowsShot, ArrowsHit) VALUES " +
                    "('" + p.getUniqueId() + "', " +
                    "'0', " +
                    "'0', " +
                    "'0', " +
                    "'0', " +
                    "'0', " +
                    "'0');");
        } else {
            GamePlayer gamePlayer = new GamePlayer(p, 0, 0, 0, 0, 0, 0);
            ResultSet snakeSgResult = MySQL.query("SELECT * FROM SnakeSG WHERE UUID='" + p.getUniqueId() + "'");
            if (snakeSgResult.next()) {
                gamePlayer.setWins(snakeSgResult.getInt("Wins"));
                gamePlayer.setKills(snakeSgResult.getInt("Kills"));
                gamePlayer.setDeaths(snakeSgResult.getInt("Deaths"));
                gamePlayer.setGPlayeds(snakeSgResult.getInt("Played"));
                gamePlayer.setAShots(snakeSgResult.getInt("ArrowsShot"));
                gamePlayer.setAHits(snakeSgResult.getInt("ArrowsHit"));
            }
            plugin.getPlayerManager().addPlayer(p, gamePlayer);
        }
    }

    public void savePlayer(Player p) {
        if (playerExist(p)) {
            GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(p);

            MySQL.update("UPDATE SnakeSG SET " +
                    "Wins='" + gamePlayer.getWins() + "', " +
                    "Kills='" + gamePlayer.getKills() + "', " +
                    "Deaths='" + gamePlayer.getDeaths() + "', " +
                    "Played='" + gamePlayer.getGPlayeds() + "', " +
                    "ArrowsShot='" + gamePlayer.getAShots() + "', " +
                    "ArrowsHit='" + gamePlayer.getAHits() + "' " +
                    "WHERE UUID='" + p.getUniqueId() + "'"
            );

            plugin.getPlayerManager().removePlayer(p);
        }
    }


    public void loadMySQL() {
        FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Settings");
        MySQL.host = config.getString("MySQL.hostname");
        MySQL.port = config.getInt("MySQL.port");
        MySQL.database = config.getString("MySQL.database");
        MySQL.username = config.getString("MySQL.username");
        MySQL.password = config.getString("MySQL.password");
        MySQL.isEnabled = config.getBoolean("MySQL.enabled");

        MySQL.connect(plugin);
        MySQL.update("CREATE TABLE IF NOT EXISTS SnakeSG (UUID VARCHAR(100), Wins Integer, Kills Integer, Deaths Integer, Played Integer, ArrowsShot Integer, ArrowsHit Integer)");
    }

    private boolean playerExist(Player p) {
        try {
            final ResultSet rs = MySQL.query("SELECT * FROM SnakeSG WHERE UUID='" + p.getUniqueId() + "'");
            return rs.next() && rs.getString("UUID") != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

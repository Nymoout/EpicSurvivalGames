package com.isnakebuzz.survivalgames.Listeners.MultiArena;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.ScoreBoard.ScoreBoardAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class JoinAndLeave implements Listener {

    private Main plugin;

    public JoinAndLeave(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");

        Player p = e.getPlayer();

        if (config.getBoolean("Lobby.teleportOnJoin")) {
            p.teleport(plugin.getLobbyManager().getLobby());
        }

        if (config.getBoolean("Lobby.LobbyScore")) {
            plugin.getScoreBoardAPI().setScoreBoard(p, ScoreBoardAPI.ScoreboardType.LOBBY);
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            try {
                plugin.getPlayerDB().loadPlayer(p);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }, 5);
    }

    @EventHandler
    public void PlayerLeftEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            arena.removePlayer(p);
            plugin.getArenaManager().getArenaPlayer().remove(p.getUniqueId());
        }

        plugin.getScoreBoardAPI().removeScoreBoard(p);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            plugin.getPlayerDB().savePlayer(p);
        });
    }


    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

package com.isnakebuzz.survivalgames.Listeners.BungeeMode;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Listeners.Custom.JoinEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class BungeeJoinAndLeave implements Listener {

    private Main plugin;

    public BungeeJoinAndLeave(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");
        Arena arena = plugin.getArenaManager().getArenas().get(config.getString("Bungee.Game"));
        if (arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING)) {
            plugin.log("Debug", "Joinable: " + (arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING)));
            e.allow();
        } else {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "In Game");
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player p = e.getPlayer();
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");

        Arena arenaS = plugin.getArenaManager().getArenas().get(config.getString("Bungee.Game"));
        JoinEvent joinEvent = new JoinEvent(p, arenaS, Enums.JoinCause.BUNGEE);
        Bukkit.getPluginManager().callEvent(joinEvent);

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
        e.setQuitMessage(null);
        Player p = e.getPlayer();


        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            arena.removePlayer(p);
            plugin.getArenaManager().getArenaPlayer().remove(p.getUniqueId());
        }
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            plugin.getPlayerDB().savePlayer(p);
        });
    }


    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

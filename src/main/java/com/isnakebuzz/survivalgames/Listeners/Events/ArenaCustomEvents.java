package com.isnakebuzz.survivalgames.Listeners.Events;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Listeners.Custom.JoinEvent;
import com.isnakebuzz.survivalgames.Listeners.Custom.LeftEvent;
import com.isnakebuzz.survivalgames.Listeners.Custom.StartEvent;
import com.isnakebuzz.survivalgames.Listeners.Custom.WinEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Player.PlayerUtils;
import com.isnakebuzz.survivalgames.Runnables.EndTask;
import com.isnakebuzz.survivalgames.Utils.Enums;
import com.isnakebuzz.survivalgames.Utils.ScoreBoard.ScoreBoardAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ArenaCustomEvents implements Listener {

    private Main plugin;

    public ArenaCustomEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void WinEvent(WinEvent e) {
        Arena arena = e.getArena();
        arena.setGameStatus(Enums.GameStatus.RESTARTING);

        plugin.getPlayerManager().getPlayer(e.getPlayer()).addWins();

        if (e.getPlayer() != null) {
            plugin.getPlayerManager().getPlayer(e.getPlayer()).addWins();
            List<String> message = plugin.getConfigUtils().getConfig(plugin, "Lang").getStringList("WinMessage");

            for (Player p : arena.getGamePlayers()) {
                for (String msg : message) {
                    p.sendMessage(c(msg
                            .replaceAll("%player%", e.getPlayer().getName())
                    ));
                }
            }

            for (Player p : arena.getSpectPlayers()) {
                for (String msg : message) {
                    p.sendMessage(c(msg
                            .replaceAll("%player%", e.getPlayer().getName())
                    ));
                }
            }
            arena.getArenaTasks().put(Enums.GameStatus.RESTARTING, Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EndTask(plugin, arena, e.getPlayer()), 0, 20).getTaskId());
        } else {
            plugin.getArenaManager().reload(arena);
        }

    }

    @EventHandler
    public void JoinEvent(JoinEvent e) {
        Player p = e.getPlayer();
        Arena arena = e.getArena();
        if ((arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING)) && arena.getGamePlayers().size() < arena.getMaxPlayers()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                arena.addPlayer(p);
            });
        } else {
            Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
            if (!p.hasPermission("sg.spectate")) {
                if (arena.getGameStatus().equals(Enums.GameStatus.INGAME)) {
                    p.sendMessage(c(lang.getString("Arena.ingame")));
                } else if (arena.getGamePlayers().size() == arena.getMaxPlayers()) {
                    p.sendMessage(c(lang.getString("Arena.full")));
                }
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    arena.setSpectator(p);
                });
                plugin.getArenaManager().getArenaPlayer().put(p.getUniqueId(), arena);
            }
        }
    }

    @EventHandler
    public void LeftEvent(LeftEvent e) {
        Arena arena = e.getArena();
        arena.removePlayer(e.getPlayer());
        PlayerUtils.clean(e.getPlayer(), GameMode.SURVIVAL, false, false, false);
        arena.getArenaVoteSys().removeFromVotes(e.getPlayer());
    }

    @EventHandler
    public void GameStart(StartEvent e) {
        e.getArena().checkVotes();
        for (Player aPlayer : e.getArena().getGamePlayers()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.getScoreBoardAPI().setGameScoreboard(aPlayer, ScoreBoardAPI.ScoreboardType.INGAME, true, false, true, e.getArena());
            });
        }
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

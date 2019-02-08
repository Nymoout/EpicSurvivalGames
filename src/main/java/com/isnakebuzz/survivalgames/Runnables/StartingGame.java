package com.isnakebuzz.survivalgames.Runnables;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Set;

public class StartingGame implements Runnable {

    private Main plugin;
    private Arena arena;

    public StartingGame(Main plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        for (Player p : arena.getGamePlayers()) {
            p.setLevel(arena.getGameStartingTimer());
        }

        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Lang");
        Set<String> keys = config.getConfigurationSection("Timer.GameStarting").getKeys(false);
        for (String time_config : keys) {
            if (arena.getGameStartingTimer() == Integer.valueOf(time_config)) {
                arena.broadcast(config.getString("Timer.GameStarting." + time_config)
                        .replaceAll("%seconds%", String.valueOf(arena.getGameStartingTimer())));
            }
        }

        for (String time_config : config.getConfigurationSection("Sounds.GameStarting").getKeys(false)) {
            if (arena.getGameStartingTimer() == Integer.valueOf(time_config)) {
                String sound = config.getString("Sounds.GameStarting." + time_config).split(";")[0];
                int volume = Integer.parseInt(config.getString("Sounds.GameStarting." + time_config).split(";")[1]);
                int pitch = Integer.parseInt(config.getString("Sounds.GameStarting." + time_config).split(";")[2]);
                for (Player p : arena.getGamePlayers()) {
                    p.playSound(p.getLocation(), Sound.valueOf(sound), volume, pitch);
                }
            }
        }

        if (arena.getGameStartingTimer() <= 1) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                arena.removeCages();
            });

            arena.setStarting(false);
            arena.getGamePlayers().forEach(players -> {
                plugin.getPlayerManager().getPlayer(players).addGPlayed();
            });
            Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
            arena.broadcast(lang.getString("Grace.start").replaceAll("%time%", String.valueOf(arena.getGracePeriod())));
            Bukkit.getScheduler().cancelTask(arena.getArenaTasks().get(Enums.GameStatus.INGAME));
            arena.getArenaTasks().remove(Enums.GameStatus.INGAME);
            arena.getArenaTasks().put(Enums.GameStatus.INGAME, Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new GameTask(plugin, arena), 0, 20).getTaskId());
        }

        arena.setGameStartingTimer(arena.getGameStartingTimer() - 1);
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

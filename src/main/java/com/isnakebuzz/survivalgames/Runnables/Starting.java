package com.isnakebuzz.survivalgames.Runnables;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Listeners.Custom.StartEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Set;

public class Starting implements Runnable {

    private Main plugin;
    private Arena arena;

    public Starting(Main plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        if (!arena.checkMinPlayers()) {
            Bukkit.getScheduler().cancelTask(arena.getArenaTasks().get(Enums.GameStatus.STARTING));
            Configuration config = plugin.getConfigUtils().getConfig(plugin, "Lang");
            arena.broadcast(config.getString("Timer.Cancelled")
                    .replaceAll("%seconds%", String.valueOf(arena.getStartingTimer())));
            String sound = config.getString("Sounds.Cancelled").split(";")[0];
            int volume = Integer.parseInt(config.getString("Sounds.Cancelled").split(";")[1]);
            int pitch = Integer.parseInt(config.getString("Sounds.Cancelled").split(";")[2]);
            for (Player p : arena.getGamePlayers()) {
                p.playSound(p.getLocation(), Sound.valueOf(sound), volume, pitch);
            }

            return;
        }

        for (Player p : arena.getGamePlayers()) {
            p.setLevel(arena.getStartingTimer());
        }

        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Lang");
        Set<String> keys = config.getConfigurationSection("Timer.Starting").getKeys(false);
        for (String time_config : keys) {
            if (arena.getStartingTimer() == Integer.valueOf(time_config)) {
                arena.broadcast(config.getString("Timer.Starting." + time_config)
                        .replaceAll("%seconds%", String.valueOf(arena.getStartingTimer())));
            }
        }

        for (String time_config : config.getConfigurationSection("Sounds.Starting").getKeys(false)) {
            if (arena.getStartingTimer() == Integer.valueOf(time_config)) {
                String sound = config.getString("Sounds.Starting." + time_config).split(";")[0];
                int volume = Integer.parseInt(config.getString("Sounds.Starting." + time_config).split(";")[1]);
                int pitch = Integer.parseInt(config.getString("Sounds.Starting." + time_config).split(";")[2]);
                for (Player p : arena.getGamePlayers()) {
                    p.playSound(p.getLocation(), Sound.valueOf(sound), volume, pitch);
                }
            }
        }

        if (arena.getStartingTimer() <= 1) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                arena.createCages();
                arena.teleportToSpawns();
            });
            StartEvent startEvent = new StartEvent(this.arena);
            Bukkit.getPluginManager().callEvent(startEvent);
            arena.setGameStatus(Enums.GameStatus.INGAME);
            arena.getArenaTasks().put(Enums.GameStatus.INGAME, Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new StartingGame(plugin, this.arena), 0, 20).getTaskId());
            Bukkit.getScheduler().cancelTask(arena.getArenaTasks().get(Enums.GameStatus.STARTING));
            arena.getArenaTasks().remove(Enums.GameStatus.STARTING);
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                plugin.getSignsManager().removeSign(this.arena);
            }, 20 * 5);
        }
        arena.setStartingTimer(arena.getStartingTimer() - 1);
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

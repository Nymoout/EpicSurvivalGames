package com.isnakebuzz.survivalgames.Runnables;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Listeners.Custom.WinEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class GameTask implements Runnable {

    private Main plugin;
    private Arena arena;

    public GameTask(Main plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        if (arena.getInGameTimer() == arena.getGracePeriod()) {
            Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
            arena.broadcast(lang.getString("Grace.finish"));
            arena.setGrace(false);
        }

        if (arena.getGamePlayers().size() <= 1) {
            if (arena.getGamePlayers().isEmpty()) {
                WinEvent winEvent = new WinEvent(null, this.arena);
                Bukkit.getPluginManager().callEvent(winEvent);
                Bukkit.getScheduler().cancelTask(arena.getArenaTasks().get(Enums.GameStatus.INGAME));
                arena.getArenaTasks().remove(Enums.GameStatus.INGAME);
                return;
            }
            WinEvent winEvent = new WinEvent(arena.getGamePlayers().get(0), this.arena);
            Bukkit.getPluginManager().callEvent(winEvent);
            Bukkit.getScheduler().cancelTask(arena.getArenaTasks().get(Enums.GameStatus.INGAME));
        }
        arena.setInGameTimer(arena.getInGameTimer() + 1);
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

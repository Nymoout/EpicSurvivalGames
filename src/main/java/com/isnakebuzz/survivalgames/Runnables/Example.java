package com.isnakebuzz.survivalgames.Runnables;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Example implements Runnable {

    private Main plugin;
    private Arena arena;

    public Example(Main plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

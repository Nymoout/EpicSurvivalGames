package com.isnakebuzz.survivalgames.Listeners;

import com.isnakebuzz.survivalgames.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public class Template implements Listener {

    private Main plugin;

    public Template(Main plugin) {
        this.plugin = plugin;
    }


    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

package com.isnakebuzz.survivalgames.Utils.Manager;

import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.LocUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class LobbyManager {

    private Main plugin;
    private Location worldLobby;

    public LobbyManager(Main plugin) {
        this.plugin = plugin;
    }

    public void removeLobby(Player p) throws IOException {
        FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Locs");
        config.set("Lobby", null);
        config.save(plugin.getConfigUtils().getFile(plugin, "Utils/Locs"));
        p.sendMessage(c("&a➠&e You has been removed &aLobby"));
        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 0);
    }

    public Location getLobby() {
        FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Locs");
        return LocUtils.stringToLoc(config.getString("Lobby"));
    }

    public void setLobby(Player p) throws IOException {
        FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Locs");
        config.set("Lobby", LocUtils.locToString(p.getLocation()));
        config.save(plugin.getConfigUtils().getFile(plugin, "Utils/Locs"));
        p.sendMessage(c("&a➠&e You has been setted &aLobby"));
        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

package com.isnakebuzz.survivalgames.Utils.Manager;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.LocUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ArenaManager {

    private Main plugin;
    private HashMap<String, Arena> arenas;
    private HashMap<UUID, Arena> arenaPlayer;

    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.arenaPlayer = new HashMap<>();
    }

    public void loadArenas() {
        File dir = new File(plugin.getDataFolder() + "/Games/");
        if (!dir.exists()) dir.mkdir();
        for (File file : dir.listFiles()) {
            String arenaName = file.getName().split(Pattern.quote("."))[0];
            try {
                loadArena(arenaName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload(Arena arena) {
        arena.resetArena();
    }

    public void loadArena(String arenaName) throws IOException {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Games/" + arenaName);
        plugin.getWorldManager().resetWorld(arenaName, b -> {
            if (b) {
                String signName = config.getString("Name");
                Location Lobby = LocUtils.stringToLoc(config.getString("Lobby"));
                Location Spectator = LocUtils.stringToLoc(config.getString("Spectator"));
                Integer minPlayers = config.getInt("Players.min");
                Integer maxPlayers = config.getInt("Players.max");
                List<Location> spawns = new ArrayList<>();
                for (String key : config.getConfigurationSection("Spawns").getKeys(false)) {
                    spawns.add(LocUtils.stringToLoc(config.getString("Spawns." + key)));
                }
                Arena arena = new Arena(plugin, arenaName, signName, Lobby, Spectator, minPlayers, maxPlayers, spawns);
                if (!this.arenas.containsKey(arenaName)) {
                    this.arenas.put(arenaName, arena);
                }
            }
        });
    }

    public void unloadArenas() {
        for (Arena arena : this.getArenas().values()) {
            arena.resetArena();
        }
    }

    public void unloadArena(String arenaName) {
        if (this.arenas.containsKey(arenaName)) {
            this.arenas.remove(arenaName);
        }
    }

    public boolean addSpawn(Player p, String arena) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            int spawn;

            try {
                spawn = arenaConfig.getConfigurationSection("Spawns").getKeys(false).size() + 1;
            } catch (Exception ex) {
                spawn = 1;
            }

            arenaConfig.set("Spawns." + spawn, LocUtils.locToString(p.getLocation()));
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been added &b#" + spawn + "&e spawn of &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }

        return true;
    }

    public boolean setLobby(Player p, String arena) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            arenaConfig.set("Lobby", LocUtils.locToString(p.getLocation()));
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been setted Lobby for &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }

        return true;
    }

    public boolean setMin(Player p, String arena, Integer minPlayers) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            arenaConfig.set("Players.min", minPlayers);
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been setted min players &b" + minPlayers + "&e for &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }
        return true;
    }

    public boolean setEnabled(Player p, String arena, Boolean enabled) throws IOException {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            arenaConfig.set("Enabled", enabled);
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been setted enabled &b" + enabled + "&e for &b" + arena + "&e arena"));

            if (enabled) {
                this.loadArena(arena);
            } else {
                this.unloadArena(arena);
            }
            plugin.getSignsManager().updateAllSigns();
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }
        return true;
    }

    public boolean setMax(Player p, String arena, Integer maxPlayers) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            arenaConfig.set("Players.max", maxPlayers);
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been setted max players &b" + maxPlayers + "&e for &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }
        return true;
    }

    public boolean setSettings(Player p, String arena, String settings, Object value) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            arenaConfig.set(settings, value);
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been setted &b" + settings + "&e for &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }

        return true;
    }

    public boolean setSpectator(Player p, String arena) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            arenaConfig.set("Spectator", LocUtils.locToString(p.getLocation()));
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been setted Spectator for &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }

        return true;
    }

    public boolean removeSpawn(Player p, String arena) {
        File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
        if (file.exists()) {
            FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
            int spawn = 0;

            try {
                spawn = arenaConfig.getConfigurationSection("Spawns").getKeys(false).size();
            } catch (Exception ex) {
                spawn = 0;
            }

            arenaConfig.set("Spawns." + spawn, null);
            try {
                arenaConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.sendMessage(c("&a➠&e Has been removed latest spawn of &b" + arena + "&e arena"));
        } else {
            p.sendMessage(c("&a➠&c Arena don't exist."));
        }

        return true;
    }

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public HashMap<UUID, Arena> getArenaPlayer() {
        return arenaPlayer;
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

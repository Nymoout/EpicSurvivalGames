package com.isnakebuzz.survivalgames.Cmds;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Listeners.Custom.JoinEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Commands implements CommandExecutor {

    private Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("SurvivalGames")) {
            if (args.length < 1) {
                sendHelp(sender);
                return false;
            }

            String subCmd = args[0];

            if (subCmd.equalsIgnoreCase("join") && sender instanceof Player) {
                if (args.length < 2) {
                    return true;
                }
                String arena = args[1];
                if (plugin.getArenaManager().getArenas().containsKey(arena)) {
                    Player p = (Player) sender;
                    if (!plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                        Arena arenaS = plugin.getArenaManager().getArenas().get(arena);
                        JoinEvent joinEvent = new JoinEvent(p, arenaS, Enums.JoinCause.COMMAND);
                        Bukkit.getPluginManager().callEvent(joinEvent);
                        sender.sendMessage(c("&aJoined to " + arenaS.getArenaName()));
                    } else {
                        sender.sendMessage(c("&cYou already in a arena"));
                    }
                } else {
                    sender.sendMessage(c("&cArena does't exist"));
                }

            }


            if (!sender.hasPermission("survivalgames.admin")) {
                return false;
            }

            if (subCmd.equalsIgnoreCase("loadWorld")) {
                if (args.length < 2) {
                    sender.sendMessage(c("&e/sg loadWorld {worldName}"));
                    return true;
                }

                String worldName = args[1];
                sender.sendMessage(c("&eLoading world" + worldName + " ...&6"));
                plugin.getWorldManager().resetWorld(worldName, isLoaded -> {
                    if (isLoaded) {
                        sender.sendMessage(c("&eHas been loaded world &6" + worldName));
                    }
                });
            }

            if (subCmd.equalsIgnoreCase("setLobby")) {
                try {
                    plugin.getLobbyManager().setLobby((Player) sender);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (subCmd.equalsIgnoreCase("arena") && sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length < 3) {
                    sendArenaHelp(sender);
                    return true;
                }
                String arena = args[1];
                String action = args[2];

                if (!p.getLocation().getWorld().getName().equals(arena)) {
                    p.sendMessage(c("&cPlease teleport to world to setup"));
                    return false;
                }

                if (action.equalsIgnoreCase("create")) {
                    if (Bukkit.getWorld(arena) == null) {
                        sender.sendMessage(c("&cWorld &b" + arena + "&c doesn't exist"));
                        sender.sendMessage(c("&cPlease import or create world with the same name of the arena"));
                        return true;
                    }
                    File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
                    if (file.exists()) {
                        p.sendMessage(c("&a➠&c Arena already exist."));
                    } else {
                        FileConfiguration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arena);
                        arenaConfig.set("Name", arena);
                        arenaConfig.set("Lobby", "none");
                        arenaConfig.set("Spectator", "none");
                        arenaConfig.set("Players.min", 2);
                        arenaConfig.set("Players.max", 0);
                        arenaConfig.set("Timer.Starting", 60);
                        arenaConfig.set("Timer.GameStarting", 15);
                        arenaConfig.set("Timer.Forced", 10);
                        arenaConfig.set("Timer.Grace", 20);
                        arenaConfig.set("Timer.End", 10);
                        arenaConfig.set("Border.Size", 0);
                        arenaConfig.set("Border.MinSize", 0);
                        arenaConfig.set("Border.Time", 0);
                        arenaConfig.set("Mode", "SOLO");
                        try {
                            arenaConfig.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        p.sendMessage(c("&a➠&e You has been created &b" + arena + "&e arena"));
                    }
                } else if (action.equalsIgnoreCase("tp")) {
                    File file = plugin.getConfigUtils().getFile(plugin, "Games/" + arena);
                    if (file.exists()) {
                        World world = Bukkit.getWorld(arena);
                        if (world != null) {
                            p.teleport(world.getSpawnLocation());
                            p.sendMessage(c("&a➠&c Teleporting to " + arena));
                        }
                    } else {
                        p.sendMessage(c("&a➠&c Arena don't exist."));
                    }
                } else if (action.equalsIgnoreCase("addSpawn")) {
                    plugin.getArenaManager().addSpawn(p, arena);
                } else if (action.equalsIgnoreCase("removeSpawn")) {
                    plugin.getArenaManager().removeSpawn(p, arena);
                } else if (action.equalsIgnoreCase("setLobby")) {
                    plugin.getArenaManager().setLobby(p, arena);
                } else if (action.equalsIgnoreCase("setSpect")) {
                    plugin.getArenaManager().setSpectator(p, arena);
                } else if (action.equalsIgnoreCase("setStarting")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setSettings(p, arena, "Timer.Starting", Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("publicName")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setSettings(p, arena, "Name", args[3]);
                } else if (action.equalsIgnoreCase("setForced")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setSettings(p, arena, "Timer.Forced", Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("setEnd")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setSettings(p, arena, "Timer.End", Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("setGameStarting")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setSettings(p, arena, "Timer.GameStarting", Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("setGrace")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setSettings(p, arena, "Timer.Grace", Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("setMin")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setMin(p, arena, Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("saveMap")) {
                    try {
                        plugin.getWorldManager().saveWorld(arena);
                        p.sendMessage(c("&a➠&e Has been saved map of &b" + arena + "&e arena"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (action.equalsIgnoreCase("setMax")) {
                    if (args.length < 4) {
                        sendArenaHelp(sender);
                        return true;
                    }
                    plugin.getArenaManager().setMax(p, arena, Integer.valueOf(args[3]));
                } else if (action.equalsIgnoreCase("enable")) {
                    try {
                        plugin.getArenaManager().setEnabled(p, arena, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        plugin.getWorldManager().saveWorld(arena);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (action.equalsIgnoreCase("disable")) {
                    try {
                        plugin.getArenaManager().setEnabled(p, arena, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (action.equalsIgnoreCase("testArenas")) {
                    for (Arena arenas : plugin.getArenaManager().getArenas().values()) {
                        p.sendMessage(c("Arenas: " + arenas.getArenaName()));
                    }
                }

            }
        }
        return false;
    }

    public void sendHelp(CommandSender p) {
        p.sendMessage(c("&e&lSurvivalGames &a&lv" + plugin.getDescription().getVersion()));
        if (p.hasPermission("survivalgames.admin")) {
            p.sendMessage(c("&e/sg setLobby &8| &7 Set a principal lobby"));
            p.sendMessage(c("&e/sg arena &8| &7 Arena commands"));
            p.sendMessage(c("&e/sg loadWorld {worldName} &8|&7 Load arena world"));
            p.sendMessage(c("&e/sg join {name} &8| &7 Join a arena"));
        } else {
            p.sendMessage(c("&e/sg join {name} &8| &7 Join a arena"));
            p.sendMessage(c(""));
            p.sendMessage(c("&cDeveloped by iSnakeBuzz_"));
        }
    }

    public void sendArenaHelp(CommandSender p) {
        p.sendMessage(c("&e&lSurvivalGames &a&lv" + plugin.getDescription().getVersion()));
        p.sendMessage(c("&e/sg arena {arenaName} tp &8| &7 Tp to arena"));
        p.sendMessage(c("&e/sg arena {arenaName} create &8| &7 Create a arena"));
        p.sendMessage(c("&e/sg arena {arenaName} publicName {signs name} &8| &7 Set name for signs"));
        p.sendMessage(c("&e/sg arena {arenaName} addSpawn &8| &7 Add spawnpoint"));
        p.sendMessage(c("&e/sg arena {arenaName} setLobby &8| &7 Set lobby"));
        p.sendMessage(c("&e/sg arena {arenaName} setSpect &8| &7 Set spectator"));
        p.sendMessage(c("&e/sg arena {arenaName} setMin {minimun players} &8| &7 Set min players"));
        p.sendMessage(c("&e/sg arena {arenaName} setMax {maximun players} &8| &7 Set max players"));
        p.sendMessage(c("&e/sg arena {arenaName} setStarting {starting time}"));
        p.sendMessage(c("&e/sg arena {arenaName} setGrace {grace period}"));
        p.sendMessage(c("&e/sg arena {arenaName} setGameStarting {game starting time}"));
        p.sendMessage(c("&e/sg arena {arenaName} setForced {starting time}"));
        p.sendMessage(c("&e/sg arena {arenaName} setEnd {starting time}"));
        p.sendMessage(c("&e/sg arena {arenaName} removeSpawn &8| &7 Remove latest spawnpoint"));
        p.sendMessage(c("&e/sg arena {arenaName} saveMap &8| &7 Save map"));
        p.sendMessage(c("&e/sg arena {arenaName} delete &8| &7 Delete arena"));
        p.sendMessage(c("&e/sg arena {arenaName} enable &8| &7 Enable arena"));
        p.sendMessage(c("&e/sg arena {arenaName} disable &8| &7 Disable arena"));
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

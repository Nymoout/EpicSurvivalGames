package com.isnakebuzz.survivalgames.Runnables;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class EndTask implements Runnable {

    private Main plugin;
    private Arena arena;
    private Player winner;

    public EndTask(Main plugin, Arena arena, Player winner) {
        this.plugin = plugin;
        this.arena = arena;
        this.winner = winner;
    }

    @Override
    public void run() {
        spawnFireworks(winner.getLocation());

        if (arena.getEndTimer() == 1) {
            Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");

            if (!arena.getSpectPlayers().isEmpty()) {
                List<Player> spectPlayers = new ArrayList<>();
                for (Player p : arena.getSpectPlayers()) {
                    spectPlayers.add(p);
                }

                for (Player p : spectPlayers) {
                    if (config.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            plugin.getPlayerDB().savePlayer(p);
                            plugin.getPlayerManager().sendLobby(p);
                        });
                    } else if (config.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                        for (Player p2 : arena.getGamePlayers()) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                p2.showPlayer(p);
                            });
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            arena.removePlayer(p);
                        });
                    }
                }

                spectPlayers.clear();
            }
            if (!arena.getGamePlayers().isEmpty()) {
                List<Player> gamePlayers = new ArrayList<>();
                for (Player p : arena.getGamePlayers()) {
                    gamePlayers.add(p);
                }

                for (Player p : gamePlayers) {
                    if (config.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            plugin.getPlayerDB().savePlayer(p);
                            plugin.getPlayerManager().sendLobby(p);
                        });
                    } else if (config.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                        for (Player p2 : arena.getSpectPlayers()) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                p2.showPlayer(p);
                            });
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            arena.removePlayer(p);
                        });
                    }
                }

                gamePlayers.clear();
            }
        }

        if (arena.getEndTimer() <= 0) {
            Bukkit.getScheduler().cancelTask(arena.getArenaTasks().get(Enums.GameStatus.RESTARTING));
            arena.getArenaTasks().remove(Enums.GameStatus.RESTARTING);
            Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");
            if (config.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                arena.resetArena();
            } else if (config.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                plugin.getServer().shutdown();
            }
        }
        arena.setEndTimer(arena.getEndTimer() - 1);
    }

    public void spawnFireworks(Location location) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Location loc = location;
            Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(1);
            fwm.addEffect(FireworkEffect.builder()
                    .withColor(Color.LIME)
                    .withColor(Color.FUCHSIA)
                    .withColor(Color.RED)
                    .flicker(true)
                    .trail(true)
                    .withFade(Color.GREEN)
                    .build()
            );
            fw.setFireworkMeta(fwm);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                fw.detonate();
            }, 14);
        });
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

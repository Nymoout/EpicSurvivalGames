package com.isnakebuzz.survivalgames.Utils.ScoreBoard;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Player.GamePlayer;
import com.isnakebuzz.survivalgames.Utils.Enums;
import com.isnakebuzz.survivalgames.Utils.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ScoreBoardAPI {

    private Main plugin;
    private HashMap<Player, Integer> scoretask;

    public ScoreBoardAPI(Main plugin) {
        this.plugin = plugin;
        this.scoretask = new HashMap<>();

    }

    public void setScoreBoard(Player p, ScoreboardType scoreboardType) {
        removeScoreBoard(p);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        ScoreBoardBuilder scoreboard = new ScoreBoardBuilder(randomString(8), false, false, false);
        int id = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/ScoreBoards");
            scoreboard.setName(chars(p, config.getString(scoreboardType.toString() + ".title")));

            int line = config.getStringList(scoreboardType.toString() + ".lines").size();
            for (final String s : config.getStringList(scoreboardType.toString() + ".lines")) {
                scoreboard.lines(line, chars(p, s));
                line--;
            }
        }, 0, 20).getTaskId();
        p.setScoreboard(scoreboard.getScoreboard());
        this.scoretask.put(p, id);
    }

    public void setGameScoreboard(Player p, ScoreboardType scoreboardType, boolean health, boolean spect, boolean gamePlayers, Arena arena) {
        removeScoreBoard(p);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        ScoreBoardBuilder scoreboard = new ScoreBoardBuilder(randomString(8), health, spect, gamePlayers);
        int id = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/ScoreBoards");
            scoreboard.setName(charsArena(p, arena, config.getString(scoreboardType.toString() + ".title")));

            int line = config.getStringList(scoreboardType.toString() + ".lines").size();
            for (final String s : config.getStringList(scoreboardType.toString() + ".lines")) {

                if (s.contains("<starting>")) {
                    if (arena.getGameStatus().equals(Enums.GameStatus.STARTING)) {
                        scoreboard.lines(line, charsArena(p, arena, s));
                    } else {
                        scoreboard.dLine(line);
                    }
                } else if (s.contains("<waiting>")) {
                    if (arena.getGameStatus().equals(Enums.GameStatus.WAITING)) {
                        scoreboard.lines(line, charsArena(p, arena, s));
                    } else {
                        scoreboard.dLine(line);
                    }
                } else if (s.contains("<cages>")) {
                    if (arena.getGameStatus().equals(Enums.GameStatus.INGAME) && arena.isStarting()) {
                        scoreboard.lines(line, charsArena(p, arena, s));
                    } else {
                        scoreboard.dLine(line);
                    }
                } else {
                    scoreboard.lines(line, charsArena(p, arena, s));
                }

                line--;
            }
            if (health) scoreboard.updatelife(arena);
            if (spect) scoreboard.updatespect(arena);
            if (gamePlayers) scoreboard.updategames(arena);
        }, 0, 20).getTaskId();
        p.setScoreboard(scoreboard.getScoreboard());
        this.scoretask.put(p, id);
    }

    public void removeScoreBoard(Player p) {
        p.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
        if (this.scoretask.containsKey(p)) Bukkit.getScheduler().cancelTask(this.scoretask.get(p));
    }

    private String charsArena(Player p, Arena arena, String c) {
        String transformed = (ChatColor.translateAlternateColorCodes('&', c)
                .replaceAll("%date%", getDate())
                .replaceAll("%online%", String.valueOf(arena.getGamePlayers().size()))
                .replaceAll("%maxPlayers%", String.valueOf(arena.getMaxPlayers()))
                .replaceAll("%map%", arena.getSignName())
                .replaceAll("%startTime%", String.valueOf(arena.getStartingTimer()))
                .replaceAll("%gameStarting%", String.valueOf(arena.getGameStartingTimer()))

                //Remove events
                .replaceAll("<waiting>", "")
                .replaceAll("<starting>", "")
                .replaceAll("<cages>", "")
        );

        if (plugin.getPlayerManager().containsPlayer(p)) {
            GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(p);
            transformed = transformed
                    .replaceAll("%kt%", String.valueOf(gamePlayer.getKillStreak()))
            ;
        }

        if (Hooks.PLACEHOLDER) {
            transformed = PlaceholderAPI.setPlaceholders(p, transformed);
        }
        return transformed;
    }

    private String chars(Player p, String c) {
        String transformed = ChatColor.translateAlternateColorCodes('&', c)
                .replaceAll("%date%", getDate());

        if (Hooks.PLACEHOLDER) {
            transformed = PlaceholderAPI.setPlaceholders(p, transformed);
        } else if (plugin.getPlayerManager().containsPlayer(p)) {
            GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(p);


            transformed = transformed
                    .replaceAll("%sg_wins%", String.valueOf(gamePlayer.getWins()))
                    .replaceAll("%sg_kills%", String.valueOf(gamePlayer.getKills()))
                    .replaceAll("%sg_deaths%", String.valueOf(gamePlayer.getDeaths()))
                    .replaceAll("%sg_ahits%", String.valueOf(gamePlayer.getAHits()))
                    .replaceAll("%sg_ashots%", String.valueOf(gamePlayer.getAShots()))
                    .replaceAll("%sg_gplayeds%", String.valueOf(gamePlayer.getGPlayeds()))
                    .replaceAll("%player%", String.valueOf(gamePlayer.getPlayer()))
            ;
        }
        return transformed;
    }

    public String getDate() {
        return new SimpleDateFormat("MM/dd/yy").format(new Date());
    }

    private String randomString(int length) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

    public enum ScoreboardType {
        LOBBY, PRELOBBY, INGAME
    }

}

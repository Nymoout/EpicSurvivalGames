package com.isnakebuzz.survivalgames.ArenaUtils;

import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Player.PlayerUtils;
import com.isnakebuzz.survivalgames.Runnables.Starting;
import com.isnakebuzz.survivalgames.Utils.Enums;
import com.isnakebuzz.survivalgames.Utils.LocUtils;
import com.isnakebuzz.survivalgames.Utils.Manager.Cages;
import com.isnakebuzz.survivalgames.Utils.ScoreBoard.ScoreBoardAPI;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arena {

    //Setupeable
    private Main plugin;
    private String arenaName;
    private String signName;
    private Location lobby;
    private Location spectator;
    private int minPlayers;
    private int maxPlayers;
    private List<Location> spawns;

    //Internal
    private List<Player> gamePlayers;
    private List<Player> spectPlayers;
    private List<Location> chestFilled;
    private List<Block> blocksToReset;
    private boolean isUsed;
    private boolean isStarted;

    private Enums.GameStatus gameStatus;

    private ArenaVoteSys arenaVoteSys;

    //Votes and gamemodes
    private Enums.ChestType chestType;
    private Enums.HealthType healthType;
    private Enums.TimeType timeType;

    //Tasks
    private Map<Enums.GameStatus, Integer> arenaTasks;

    //Timers
    private int startingTimer;
    private int gameStartingTimer;
    private int forcedGame;
    private int inGameTimer;
    private int gracePeriod;
    private int endTimer;

    private boolean isGrace;
    private boolean starting;

    public Arena(Main plugin, String arenaName, String signName, Location lobby, Location spectator, int minPlayers, int maxPlayers, List<Location> spawns) {
        this.plugin = plugin;
        this.arenaName = arenaName;
        this.signName = signName;
        this.lobby = lobby;
        this.spectator = spectator;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.spawns = spawns;

        //Debug:
        plugin.log("Debug", "Location of lobby " + lobby);

        //Internal use
        this.arenaVoteSys = new ArenaVoteSys(this);
        this.gamePlayers = new ArrayList<>();
        this.spectPlayers = new ArrayList<>();
        this.blocksToReset = new ArrayList<>();
        this.gameStatus = Enums.GameStatus.WAITING;
        this.isUsed = false;
        this.isStarted = false;
        this.isGrace = true;
        this.starting = true;
        this.arenaTasks = new HashMap<>();
        this.chestFilled = new ArrayList<>();
        this.chestType = Enums.ChestType.NORMAL;
        this.healthType = Enums.HealthType.NORMAL;
        this.timeType = Enums.TimeType.DAY;

        //Timers
        Configuration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arenaName);
        this.startingTimer = arenaConfig.getInt("Timer.Starting");
        this.gameStartingTimer = arenaConfig.getInt("Timer.GameStarting");
        this.forcedGame = arenaConfig.getInt("Timer.Forced");
        this.gracePeriod = arenaConfig.getInt("Timer.Grace");
        this.endTimer = arenaConfig.getInt("Timer.End");
        this.inGameTimer = 0;
    }

    public void resetArena() {
        //Arena restarting..
        System.out.println("Restarting arena..");
        plugin.getWorldManager().resetWorld(this.arenaName, value -> {
            if (value) {
                //World reset
                World w = Bukkit.getWorld(this.arenaName);
                w.setAutoSave(false);
                w.setGameRuleValue("doMobSpawning", "false");
                w.setGameRuleValue("doDaylightCycle", "false");
                w.setGameRuleValue("commandBlockOutput", "false");
                w.setTime(0L);
                w.setDifficulty(Difficulty.NORMAL);

                removeEntities(w.getEntities());

                //Internal use
                this.arenaVoteSys = new ArenaVoteSys(this);
                this.gamePlayers = new ArrayList<>();
                this.spectPlayers = new ArrayList<>();
                this.blocksToReset = new ArrayList<>();
                this.isUsed = false;
                this.isStarted = false;
                this.isGrace = true;
                this.starting = true;
                this.arenaTasks = new HashMap<>();
                this.chestFilled = new ArrayList<>();
                this.chestType = Enums.ChestType.NOBODY;
                this.healthType = Enums.HealthType.NOBODY;
                this.timeType = Enums.TimeType.NOBODY;

                //Timers
                Configuration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arenaName);
                this.startingTimer = arenaConfig.getInt("Timer.Starting");
                this.gameStartingTimer = arenaConfig.getInt("Timer.GameStarting");
                this.forcedGame = arenaConfig.getInt("Timer.Forced");
                this.gracePeriod = arenaConfig.getInt("Timer.Grace");
                this.endTimer = arenaConfig.getInt("Timer.End");
                this.inGameTimer = 0;
                this.gameStatus = Enums.GameStatus.WAITING;
                plugin.getSignsManager().updateAllSigns();
            }
        });
    }

    public void stopArena() {
        setGameStatus(Enums.GameStatus.WAITING);
        setStarted(false);

        Configuration arenaConfig = plugin.getConfigUtils().getConfig(plugin, "Games/" + arenaName);
        this.startingTimer = arenaConfig.getInt("Timer.Starting");
        this.gameStartingTimer = arenaConfig.getInt("Timer.GameStarting");
        this.forcedGame = arenaConfig.getInt("Timer.Forced");
        this.gracePeriod = arenaConfig.getInt("Timer.Grace");
        this.endTimer = arenaConfig.getInt("Timer.End");
        this.inGameTimer = 0;
    }

    public String getArenaName() {
        return arenaName;
    }

    public String getSignName() {
        return signName;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpectator() {
        return spectator;
    }

    public void setSpectator(Player p) {
        p.setHealth(p.getMaxHealth());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            //p.teleport(getSpectator());
            PlayerUtils.clean(p, GameMode.ADVENTURE, true, true, true);
            plugin.getInventories().setSpectInventory(p);
        }, 2);
        this.getSpectPlayers().add(p);

        //Setup spects settings
        for (Player aPlayer : this.getGamePlayers()) {
            aPlayer.hidePlayer(p);
        }
        for (Player aPlayer : this.getSpectPlayers()) {
            aPlayer.hidePlayer(p);
        }

        plugin.getScoreBoardAPI().setGameScoreboard(p, ScoreBoardAPI.ScoreboardType.PRELOBBY, true, true, true, this);
        if (this.getGamePlayers().contains(p)) {
            this.getGamePlayers().remove(p);
        }
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public List<Player> getGamePlayers() {
        return gamePlayers;
    }

    public List<Player> getSpectPlayers() {
        return spectPlayers;
    }

    public Enums.GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Enums.GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        plugin.getSignsManager().updateAllSigns();
    }

    public boolean isGrace() {
        return isGrace;
    }

    public void setGrace(boolean grace) {
        isGrace = grace;
    }

    public List<Location> getChestFilled() {
        return chestFilled;
    }

    public Enums.ChestType getChestType() {
        return chestType;
    }

    public void setChestType(Enums.ChestType chestType) {
        this.chestType = chestType;
    }

    public int getStartingTimer() {
        return startingTimer;
    }

    public void setStartingTimer(int startingTimer) {
        this.startingTimer = startingTimer;
    }

    public int getForcedGame() {
        return forcedGame;
    }

    public int getInGameTimer() {
        return inGameTimer;
    }

    public void setInGameTimer(int inGameTimer) {
        this.inGameTimer = inGameTimer;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public int getEndTimer() {
        return endTimer;
    }

    public void setEndTimer(int endTimer) {
        this.endTimer = endTimer;
    }

    public int getGameStartingTimer() {
        return gameStartingTimer;
    }

    public void setGameStartingTimer(int gameStartingTimer) {
        this.gameStartingTimer = gameStartingTimer;
    }

    public Map<Enums.GameStatus, Integer> getArenaTasks() {
        return arenaTasks;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public ArenaVoteSys getArenaVoteSys() {
        return arenaVoteSys;
    }

    public Enums.HealthType getHealthType() {
        return healthType;
    }

    public void setHealthType(Enums.HealthType healthType) {
        this.healthType = healthType;
    }

    public Enums.TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(Enums.TimeType timeType) {
        this.timeType = timeType;
    }

    public String getStatusMsg() {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Signs");
        if (getGameStatus().equals(Enums.GameStatus.LOADING)) {
            return c(config.getString("Status.Loading.msg"));
        } else if (getGameStatus().equals(Enums.GameStatus.WAITING)) {
            return c(config.getString("Status.Waiting.msg"));
        } else if (getGameStatus().equals(Enums.GameStatus.INGAME)) {
            return c(config.getString("Status.InGame.msg"));
        } else if (this.getGamePlayers().size() >= this.getMaxPlayers()) {
            return c(config.getString("Status.Full.msg"));
        } else if (getGameStatus().equals(Enums.GameStatus.STARTING)) {
            return c(config.getString("Status.Starting.msg"));
        }
        return "none";
    }

    public int getBlockStatus() {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Signs");
        if (getGameStatus().equals(Enums.GameStatus.LOADING)) {
            return config.getInt("Status.Loading.glass");
        } else if (getGameStatus().equals(Enums.GameStatus.WAITING)) {
            return config.getInt("Status.Waiting.glass");
        } else if (getGameStatus().equals(Enums.GameStatus.INGAME)) {
            return config.getInt("Status.InGame.glass");
        } else if (this.getGamePlayers().size() >= this.getMaxPlayers()) {
            return config.getInt("Status.Full.glass");
        } else if (getGameStatus().equals(Enums.GameStatus.STARTING)) {
            return config.getInt("Status.Starting.glass");
        }
        return 0;
    }

    public void addPlayer(Player p) {
        Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
        p.teleport(this.getLobby());
        PlayerUtils.clean(p, GameMode.ADVENTURE, false, false, false);
        this.getGamePlayers().add(p);
        plugin.getArenaManager().getArenaPlayer().put(p.getUniqueId(), this);
        plugin.getScoreBoardAPI().setGameScoreboard(p, ScoreBoardAPI.ScoreboardType.PRELOBBY, false, true, false, this);
        plugin.getInventories().setLobbyInventory(p);

        if (this.chekcStart() && !isStarted) {
            this.arenaTasks.put(Enums.GameStatus.STARTING, Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Starting(plugin, this), 0, 20).getTaskId());
            this.isStarted = true;
            setGameStatus(Enums.GameStatus.STARTING);
        }

        //Updating signs
        plugin.getSignsManager().updateAllSigns();

        broadcast(lang.getString("JoinMessage")
                .replaceAll("%player%", p.getName())
                .replaceAll("%playing%", String.valueOf(this.getGamePlayers().size()))
                .replaceAll("%max%", String.valueOf(this.getMaxPlayers()))
        );
    }

    public void removePlayer(Player p) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");
        Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
        Configuration settings = plugin.getConfigUtils().getConfig(plugin, "Settings");


        if (this.getGameStatus().equals(Enums.GameStatus.INGAME) || this.getGameStatus().equals(Enums.GameStatus.RESTARTING)) {
            if (this.getGamePlayers().contains(p)) {
                PlayerUtils.clean(p, GameMode.SURVIVAL, false, false, false);
                this.getGamePlayers().remove(p);
                plugin.getArenaManager().getArenaPlayer().remove(p.getUniqueId());
            } else if (this.getSpectPlayers().contains(p)) {
                PlayerUtils.clean(p, GameMode.SURVIVAL, false, false, false);
                this.getSpectPlayers().remove(p);
                plugin.getArenaManager().getArenaPlayer().remove(p.getUniqueId());
            }
        } else {
            PlayerUtils.clean(p, GameMode.SURVIVAL, false, false, false);
            this.getGamePlayers().remove(p);
            plugin.getArenaManager().getArenaPlayer().remove(p.getUniqueId());

            //Updating signs
            broadcast(lang.getString("LeaveMessage")
                    .replaceAll("%player%", p.getName())
                    .replaceAll("%playing%", String.valueOf(this.getGamePlayers().size()))
                    .replaceAll("%max%", String.valueOf(this.getMaxPlayers()))
            );
        }

        if (settings.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
            p.teleport(plugin.getLobbyManager().getLobby());
            if (config.getBoolean("Lobby.LobbyScore")) {
                plugin.getScoreBoardAPI().setScoreBoard(p, ScoreBoardAPI.ScoreboardType.LOBBY);
            } else {
                plugin.getScoreBoardAPI().removeScoreBoard(p);
            }
            plugin.getSignsManager().updateAllSigns();
        } else if (settings.getString("Mode").equalsIgnoreCase("BUNGEE")) {
            plugin.getPlayerManager().connect(p, settings.getString("Bungee.Lobby"));
        }
    }

    public void checkVotes() {
        World world = Bukkit.getWorld(arenaName);
        this.arenaVoteSys.setTypes();

        Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
        broadcast(lang.getString("Votes.Chest." + this.chestType.toString()));
        broadcast(lang.getString("Votes.Time." + this.timeType.toString()));
        broadcast(lang.getString("Votes.Health." + this.healthType.toString()));

        if (this.timeType.equals(Enums.TimeType.DAY) || this.timeType.equals(Enums.TimeType.NOBODY)) {
            world.setTime(1000);
        } else if (this.timeType.equals(Enums.TimeType.SUNSET)) {
            world.setTime(12000);
        } else if (this.timeType.equals(Enums.TimeType.NIGHT)) {
            world.setTime(13000);
        }

        if (this.healthType.equals(Enums.HealthType.HARD)) {
            for (Player p : this.getGamePlayers()) {
                p.setHealthScale(20);
            }
        } else if (this.healthType.equals(Enums.HealthType.NORMAL) || this.healthType.equals(Enums.HealthType.NOBODY)) {
            for (Player p : this.getGamePlayers()) {
                p.setHealthScale(20);
            }
        } else if (this.healthType.equals(Enums.HealthType.DOUBLE)) {
            for (Player p : this.getGamePlayers()) {
                p.setHealthScale(40);
            }
        } else if (this.healthType.equals(Enums.HealthType.TRIPLE)) {
            for (Player p : this.getGamePlayers()) {
                p.setHealthScale(60);
            }
        }
    }

    public void broadcast(String message) {
        for (Player p : this.getGamePlayers()) {
            p.sendMessage(c(message));
        }
        for (Player p : this.getSpectPlayers()) {
            p.sendMessage(c(message));
        }
    }

    public void teleportToSpawns() {
        int i = 0;
        for (Player p : this.getGamePlayers()) {
            PlayerUtils.clean(p, GameMode.SURVIVAL, false, false, false);
            p.teleport(this.getSpawns().get(i));
            if (i < this.getSpawns().size()) {
                i++;
            }
        }
    }

    private boolean chekcStart() {
        if (this.getGamePlayers().size() >= this.getMinPlayers()) {
            return true;
        }
        return false;
    }

    public boolean checkMinPlayers() {
        if (this.getGamePlayers().size() >= this.getMinPlayers()) {
            return true;
        }
        return false;
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

    public void createCages() {
        for (Location loc : this.getSpawns()) {
            Cages.cage(loc, Material.BARRIER);
        }
    }

    public void removeCages() {
        for (Location loc : this.getSpawns()) {
            Cages.cage(loc, Material.AIR);
        }
    }

    private void removeEntities(List<Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof Item) {
                entity.remove();
            }
        }
    }

    public List<Block> getBlocksToReset() {
        return blocksToReset;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isEmpty() {
        return this.gamePlayers.isEmpty();
    }
}

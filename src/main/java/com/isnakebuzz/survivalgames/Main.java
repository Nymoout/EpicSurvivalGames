package com.isnakebuzz.survivalgames;

import com.isnakebuzz.survivalgames.Chests.ChestController;
import com.isnakebuzz.survivalgames.Cmds.Commands;
import com.isnakebuzz.survivalgames.Cmds.LeaveCommand;
import com.isnakebuzz.survivalgames.Configurations.ConfigCreator;
import com.isnakebuzz.survivalgames.Configurations.ConfigUtils;
import com.isnakebuzz.survivalgames.Database.MySQL;
import com.isnakebuzz.survivalgames.Database.PlayerDB;
import com.isnakebuzz.survivalgames.Inventory.Inventories;
import com.isnakebuzz.survivalgames.Listeners.EventManager;
import com.isnakebuzz.survivalgames.Signs.SignsManager;
import com.isnakebuzz.survivalgames.Utils.Hooks;
import com.isnakebuzz.survivalgames.Utils.Leaderheads.*;
import com.isnakebuzz.survivalgames.Utils.Manager.*;
import com.isnakebuzz.survivalgames.Utils.PlaceHolders.SurvivalGamesHolders;
import com.isnakebuzz.survivalgames.Utils.Plugin.Metrics;
import com.isnakebuzz.survivalgames.Utils.ScoreBoard.ScoreBoardAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ConfigUtils configUtils;
    private LobbyManager lobbyManager;
    private EventManager eventManager;
    private ArenaManager arenaManager;
    private SignsManager signsManager;
    private ScoreBoardAPI scoreBoardAPI;
    private Inventories inventories;
    private ChestController chestController;
    private PlayerManager playerManager;
    private PlayerDB playerDB;
    private WorldManager worldManager;
    private FileManager fileManager;

    public Main() {
        this.fileManager = new FileManager(this);
        this.worldManager = new WorldManager(this);
        this.playerDB = new PlayerDB(this);
        this.playerManager = new PlayerManager(this);
        this.chestController = new ChestController(this);
        this.inventories = new Inventories(this);
        this.scoreBoardAPI = new ScoreBoardAPI(this);
        this.signsManager = new SignsManager(this);
        this.arenaManager = new ArenaManager(this);
        this.eventManager = new EventManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.configUtils = new ConfigUtils();
    }

    @Override
    public void onEnable() {
        //Loading configs
        ConfigCreator.get().setup(this, "Settings");
        ConfigCreator.get().setup(this, "Signs");
        ConfigCreator.get().setup(this, "Lang");
        ConfigCreator.get().setup(this, "Utils/Locs");
        ConfigCreator.get().setup(this, "Utils/ScoreBoards");
        ConfigCreator.get().setup(this, "Utils/Inventory");
        ConfigCreator.get().setup(this, "Utils/MenuCreator");

        this.getCommand("survivalgames").setExecutor(new Commands(this));
        this.getCommand("leave").setExecutor(new LeaveCommand(this));

        this.playerDB.loadMySQL();
        this.eventManager.loadEvents();
        this.arenaManager.loadArenas();
        this.signsManager.initSigns();
        this.getChestController().load();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                signsManager.updateAllSigns();
            }
        }, 20);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Hooks.PLACEHOLDER = true;
            new SurvivalGamesHolders(this).hook();
            log("Hooked", "PlaceHolderAPI");
        }

        if (Bukkit.getPluginManager().getPlugin("LeaderHeads") != null) {
            Hooks.LEADERHEADS = true;
            new Wins(this, "wins");
            new Kills(this, "kills");
            new Deaths(this, "deaths");
            new GamePlayeds(this, "gplayeds");
            new ArrowHits(this, "ahits");
            new ArrowShots(this, "ashots");
            log("Hooked", "LeaderHeads");
        }

        //Loading metrics
        Metrics metrics = new Metrics(this);

        log("SurvivalGames", "Has been loaded");
    }

    @Override
    public void onDisable() {
        this.arenaManager.unloadArenas();
        MySQL.disconnect();
    }

    public void log(String logger, String log) {
        Bukkit.getConsoleSender().sendMessage(c("&a&l" + logger + " &8|&e " + log));
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SignsManager getSignsManager() {
        return signsManager;
    }

    public ScoreBoardAPI getScoreBoardAPI() {
        return scoreBoardAPI;
    }

    public Inventories getInventories() {
        return inventories;
    }

    public ChestController getChestController() {
        return chestController;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PlayerDB getPlayerDB() {
        return playerDB;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

package com.isnakebuzz.survivalgames.Listeners;

import com.isnakebuzz.survivalgames.Listeners.BungeeMode.BungeeJoinAndLeave;
import com.isnakebuzz.survivalgames.Listeners.Events.ArenaCustomEvents;
import com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents.BlocksEvent;
import com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents.ExtraEvents;
import com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents.InteractEvents;
import com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents.PlayerStats;
import com.isnakebuzz.survivalgames.Listeners.Events.DeathMsg.EventDie;
import com.isnakebuzz.survivalgames.Listeners.Events.DeathMsg.EventTag;
import com.isnakebuzz.survivalgames.Listeners.Events.SpectatorEvents;
import com.isnakebuzz.survivalgames.Listeners.MultiArena.JoinAndLeave;
import com.isnakebuzz.survivalgames.Listeners.Signs.EventSigns;
import com.isnakebuzz.survivalgames.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class EventManager {

    private Main plugin;

    public EventManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadEvents() {
        plugin.log("SurvivalGames", "Loading listeners..");

        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");
        if (config.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
            registerListener(new JoinAndLeave(plugin));
        } else if (config.getString("Mode").equalsIgnoreCase("BUNGEE")) {
            registerListener(new BungeeJoinAndLeave(plugin));
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            EventTag.increaseTimers();
        }, 20, 20);

        registerListener(new EventSigns(plugin));
        registerListener(new ExtraEvents(plugin));
        registerListener(new ArenaCustomEvents(plugin));
        registerListener(new EventDie(plugin));
        registerListener(new EventTag(plugin));
        registerListener(new SpectatorEvents(plugin));
        registerListener(new BlocksEvent(plugin));
        registerListener(new InteractEvents(plugin));
        registerListener(new PlayerStats(plugin));

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    // Loading Listeners
    public void registerListener(Listener listener) {
        plugin.log("SurvivalGames", "&5-&e Loaded listener &a" + listener.getClass().getSimpleName());
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void unregisterListener(Listener listener) {
        plugin.log("SurvivalGames", "&5-&e Unloading listener &a" + listener.getClass().getSimpleName());
        HandlerList.unregisterAll(listener);
    }

}

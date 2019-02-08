package com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Inventory.MenuManager.MenuCreator;
import com.isnakebuzz.survivalgames.Inventory.Utils.ItemBuilder;
import com.isnakebuzz.survivalgames.Listeners.Custom.LeftEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class ExtraEvents implements Listener {

    private Main plugin;

    public ExtraEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void DropItems(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING) || arena.isStarting()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void LossHungry(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING) || arena.isGrace()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
                if (arena.getHealthType().equals(Enums.HealthType.HARD)) {
                    if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || e.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void worldInit(WorldInitEvent e) {
        World w = e.getWorld();
        w.setTime(6000);
        w.setGameRuleValue("doDaylightCycle", "false");
        w.setGameRuleValue("doMobSpawning", "false");
        w.setWeatherDuration(0);
        w.setAmbientSpawnLimit(0);
        w.setAutoSave(false);
        w.setDifficulty(Difficulty.NORMAL);
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

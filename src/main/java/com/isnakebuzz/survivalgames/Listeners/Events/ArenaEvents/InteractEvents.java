package com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvents implements Listener {

    private Main plugin;

    public InteractEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getGameStatus().equals(Enums.GameStatus.INGAME) && !arena.isStarting()) {
                if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.CHEST) && !arena.getChestFilled().contains(e.getClickedBlock().getLocation())) {
                    final BlockState block = e.getClickedBlock().getState();
                    if (block instanceof Chest) {
                        Chest chest = (Chest) block;
                        plugin.getChestController().setChest(arena, chest);
                        arena.getChestFilled().add(e.getClickedBlock().getLocation());
                    }
                }
            }
        }
    }

    @EventHandler
    public void EntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player player = (Player) e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(damager.getUniqueId()) && plugin.getArenaManager().getArenaPlayer().containsKey(player.getUniqueId())) {
                Arena arena = plugin.getArenaManager().getArenaPlayer().get(damager.getUniqueId());
                if (arena.getSpectPlayers().contains(damager) && arena.getGamePlayers().contains(player)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void PlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
                if (arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING) || arena.isGrace()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getGameStatus().equals(Enums.GameStatus.WAITING) || arena.getGameStatus().equals(Enums.GameStatus.STARTING) || arena.isStarting()) {
                e.setCancelled(true);
            }
        }
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

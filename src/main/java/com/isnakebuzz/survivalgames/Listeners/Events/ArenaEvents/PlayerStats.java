package com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerStats implements Listener {

    private Main plugin;

    public PlayerStats(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void ArrowShot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                plugin.getPlayerManager().getPlayer(p).addAShots();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void ArrowHits(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Arrow) {
                final Arrow a = (Arrow) e.getDamager();
                if (a.getShooter() instanceof Player) {
                    a.getShooter();
                    final Player p = (Player) a.getShooter();
                    if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                        plugin.getPlayerManager().getPlayer(p).addAHits();
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                e.setDeathMessage(null);
                Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
                if (arena.getGameStatus().equals(Enums.GameStatus.INGAME)) {
                    if (p.getHealth() < 0.5) {
                        p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
                        arena.setSpectator(p);
                        plugin.getPlayerManager().getPlayer(p).addDeaths();
                        plugin.getPlayerManager().getPlayer(p).setKillStreak(0);
                    }
                }
            }
        }

        if (e.getEntity().getKiller() instanceof Player) {
            Player p = e.getEntity().getKiller();
            plugin.getPlayerManager().getPlayer(p).addKills();
            plugin.getPlayerManager().getPlayer(p).addKt();
        }
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

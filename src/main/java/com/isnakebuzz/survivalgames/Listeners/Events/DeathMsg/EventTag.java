package com.isnakebuzz.survivalgames.Listeners.Events.DeathMsg;

import com.isnakebuzz.survivalgames.Main;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class EventTag implements Listener {


    public static HashMap<LivingEntity, LivingEntity> lastDamager;
    public static HashMap<LivingEntity, Integer> lastDmgTime;
    private static Main plugin;

    static {
        EventTag.lastDamager = new HashMap<LivingEntity, LivingEntity>();
        EventTag.lastDmgTime = new HashMap<LivingEntity, Integer>();
    }

    public EventTag(Main plugin) {
        this.plugin = plugin;
    }

    public static boolean getInflicted(final LivingEntity e) {
        return EventTag.lastDamager.containsKey(e) && EventTag.lastDmgTime.get(e) <= plugin.getConfigUtils().getConfig(plugin, "Settings").getInt("CombatTime");
    }

    public static LivingEntity getWho(final LivingEntity e) {
        if (EventTag.lastDamager.containsKey(e)) {
            return EventTag.lastDamager.get(e);
        }
        return null;
    }

    public static void increaseTimers() {
        final ArrayList<LivingEntity> entities = new ArrayList<LivingEntity>(EventTag.lastDmgTime.keySet());
        for (final LivingEntity e : entities) {
            if (e.isDead() || !e.isValid()) {
                EventTag.lastDmgTime.remove(e);
                EventTag.lastDamager.remove(e);
            } else if (EventTag.lastDmgTime.get(e) <= plugin.getConfigUtils().getConfig(plugin, "Settings").getInt("CombatTime")) {
                EventTag.lastDmgTime.put(e, EventTag.lastDmgTime.get(e) - 1);
            } else {
                EventTag.lastDmgTime.remove(e);
                EventTag.lastDamager.remove(e);
            }
        }
    }

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                if (e.getEntity() != null && e.getDamager() != null && e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
                    final LivingEntity attacker = (LivingEntity) e.getDamager();
                    final LivingEntity damaged = (LivingEntity) e.getEntity();
                    EventTag.lastDamager.put(damaged, attacker);
                    EventTag.lastDmgTime.put(damaged, 0);
                } else if (e.getEntity() != null && e.getDamager() != null && e.getDamager() instanceof Projectile && e.getDamager().getCustomName() != null) {
                    for (final LivingEntity en : e.getDamager().getWorld().getLivingEntities()) {
                        if (en.getEntityId() == Integer.parseInt(e.getDamager().getCustomName())) {
                            final LivingEntity damaged2 = (LivingEntity) e.getEntity();
                            EventTag.lastDamager.put(damaged2, en);
                            EventTag.lastDmgTime.put(damaged2, 0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShoot(final EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                e.getProjectile().setCustomName(Integer.toString(e.getEntity().getEntityId()));
            }
        }
    }

    @EventHandler
    public void onLaunch(final ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                if (e.getEntity().getShooter() instanceof LivingEntity) {
                    final LivingEntity shooter = (LivingEntity) e.getEntity().getShooter();
                    e.getEntity().setCustomName(Integer.toString(shooter.getEntityId()));
                }
            }
        }
    }
}

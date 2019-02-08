package com.isnakebuzz.survivalgames.Listeners.Events.ArenaEvents;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BlocksEvent implements Listener {

    private Main plugin;

    public BlocksEvent(Main plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void BreakEvent(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlaceEvent(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            Block b = e.getBlockClicked().getRelative(e.getBlockFace());
            if (!arena.getBlocksToReset().contains(b) && b.isLiquid()) {
                arena.getBlocksToReset().add(b);
            }
            b.breakNaturally();
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            Block b = e.getBlockClicked().getRelative(e.getBlockFace());
            if (arena.getBlocksToReset().contains(b) && b.isLiquid()) {
                arena.getBlocksToReset().remove(b);
            }
        }
    }

    @EventHandler
    public void blockFormToEvent(BlockFromToEvent e) {
        if (!e.getToBlock().getType().equals(Material.AIR)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockFormEvent(BlockFormEvent e) {
        /*if (!e.getBlock().getType().equals(Material.AIR)) {
            e.setCancelled(true);
        }*/
    }

    @EventHandler
    public void blockObsidian(BlockPhysicsEvent e) {
        /*if (e.getChangedType().equals(Material.OBSIDIAN)) {
            e.setCancelled(true);
        }*/
    }

    @EventHandler
    public void blockGrownEvent(BlockGrowEvent e) {
       // e.setCancelled(true);
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

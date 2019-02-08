package com.isnakebuzz.survivalgames.Listeners.Signs;

import com.isnakebuzz.survivalgames.Listeners.Custom.JoinEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Signs.SignBlock;
import com.isnakebuzz.survivalgames.Signs.utils.LocationUtil;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventSigns implements Listener {

    private Main plugin;

    public EventSigns(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(SignChangeEvent e) throws IOException {
        final Block block = e.getBlock();
        if (e.getPlayer().hasPermission("survivalgames.admin") && (e.getLine(0).equalsIgnoreCase("[SurvivalGames]") || e.getLine(0).equalsIgnoreCase("[sg]"))) {
            if (block.getType() == Material.WALL_SIGN) {
                FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Signs");
                List<String> list;
                if (config.getStringList("Signs") == null) {
                    list = new ArrayList<>();
                } else {
                    list = config.getStringList("Signs");
                }
                list.add(LocationUtil.getString(block.getLocation(), false));
                config.set("Signs", list);

                config.save(plugin.getConfigUtils().getFile(plugin, "Signs"));
                e.getPlayer().sendMessage("§aAdded game sign");

                //Added to new sign
                SignBlock signBlock = new SignBlock(LocationUtil.getString(block.getLocation(), false));
                plugin.getSignsManager().addSign(LocationUtil.getString(block.getLocation(), false), signBlock);
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    plugin.getSignsManager().searchingGames(signBlock);
                    plugin.getSignsManager().updateAllSigns();
                }, 5);
            } else {
                e.getPlayer().sendMessage("§cThis sign must be placed on a wall");
            }
        }
    }

    @EventHandler
    public void onSignBreak(final BlockBreakEvent e) throws IOException {
        if (e.getBlock().getType() == Material.WALL_SIGN && e.getPlayer().hasPermission("survivalgames.admin")) {
            FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Signs");
            String loc = LocationUtil.getString(e.getBlock().getLocation(), false);
            if (config.getStringList("Signs").contains(loc)) {
                if (plugin.getSignsManager().getSigns().get(loc).getGame() != null) {
                    plugin.getSignsManager().removeSign(plugin.getSignsManager().getSigns().get(loc).getGame());
                }
                if (plugin.getSignsManager().getSigns().containsKey(loc)) {
                    plugin.getSignsManager().getSigns().remove(loc);
                }
                List<String> list = config.getStringList("Signs");
                list.remove(loc);
                config.set("Signs", list);

                config.save(plugin.getConfigUtils().getFile(plugin, "Signs"));
                e.getPlayer().sendMessage(c("&aRemoved game sign"));
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && e.hasBlock() && e.getClickedBlock().getState() instanceof org.bukkit.block.Sign) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (SignBlock signBlock : plugin.getSignsManager().getSigns().values()) {
                    if (signBlock.getGame() != null) {
                        if (signBlock.getLocation().equalsIgnoreCase(LocationUtil.getString(e.getClickedBlock().getLocation(), false))) {
                            JoinEvent joinEvent = new JoinEvent(e.getPlayer(), signBlock.getGame(), Enums.JoinCause.SIGN);
                            Bukkit.getPluginManager().callEvent(joinEvent);
                            break;
                        }
                    }
                }
            });
        }
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

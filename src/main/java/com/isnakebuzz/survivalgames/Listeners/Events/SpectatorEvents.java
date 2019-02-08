package com.isnakebuzz.survivalgames.Listeners.Events;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Inventory.MenuManager.MenuCreator;
import com.isnakebuzz.survivalgames.Inventory.Utils.ItemBuilder;
import com.isnakebuzz.survivalgames.Listeners.Custom.LeftEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class SpectatorEvents implements Listener {

    private Main plugin;

    public SpectatorEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void FoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getSpectPlayers().contains(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerPickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getSpectPlayers().contains(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            if (arena.getSpectPlayers().contains(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void LobbyItemsInteract(InventoryClickEvent e) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Inventory");
        Configuration settings = plugin.getConfigUtils().getConfig(plugin, "Settings");

        Set<String> key;
        try {
            key = config.getConfigurationSection("Lobby").getKeys(false);
        } catch (Exception ex) {
            key = null;
        }
        if (key == null | key.size() < 1) return;
        Player p = (Player) e.getWhoClicked();

        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            for (String item : key) {
                String path = "Lobby." + item + ".";
                String _item = config.getString(path + "item");
                String _name = config.getString(path + "name");
                List<String> _lore = config.getStringList(path + "lore");
                String _action = config.getString(path + "action");
                ItemStack itemStack = ItemBuilder.crearItem1(Integer.valueOf(_item.split(":")[0]), 1, Integer.valueOf(_item.split(":")[1]), _name, _lore);

                if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

                if (e.getCurrentItem().equals(itemStack)) {
                    if (_action.split(":")[0].equalsIgnoreCase("open")) {
                        new MenuCreator(p, plugin, _action.split(":")[1], arena).o(p);
                    } else if (_action.split(":")[0].equalsIgnoreCase("cmd")) {
                        String cmd = "/" + _action.split(":")[1];
                        p.chat(cmd);
                    } else if (_action.split(":")[0].equalsIgnoreCase("leave")) {
                        if (settings.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.INTERACT);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        } else if (settings.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.BUNGEE);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        }

                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void LobbyItemsInteract(PlayerInteractEvent e) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Inventory");
        Configuration settings = plugin.getConfigUtils().getConfig(plugin, "Settings");

        Set<String> key;
        try {
            key = config.getConfigurationSection("Lobby").getKeys(false);
        } catch (Exception ex) {
            key = null;
        }
        if (key == null | key.size() < 1) return;
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            for (String item : key) {
                String path = "Lobby." + item + ".";
                String _item = config.getString(path + "item");
                String _name = config.getString(path + "name");
                List<String> _lore = config.getStringList(path + "lore");
                String _action = config.getString(path + "action");
                ItemStack itemStack = ItemBuilder.crearItem1(Integer.valueOf(_item.split(":")[0]), 1, Integer.valueOf(_item.split(":")[1]), _name, _lore);

                if (e.getItem() == null || e.getItem().getItemMeta().equals(null)) return;

                if (e.getItem().equals(itemStack)) {
                    if (_action.split(":")[0].equalsIgnoreCase("open")) {
                        new MenuCreator(e.getPlayer(), plugin, _action.split(":")[1], arena).o(e.getPlayer());
                    } else if (_action.split(":")[0].equalsIgnoreCase("cmd")) {
                        String cmd = "/" + _action.split(":")[1];
                        e.getPlayer().chat(cmd);
                    } else if (_action.split(":")[0].equalsIgnoreCase("leave")) {
                        if (settings.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.INTERACT);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        } else if (settings.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.BUNGEE);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        }
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void SpectatorItemsInteract(InventoryClickEvent e) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Inventory");
        Configuration settings = plugin.getConfigUtils().getConfig(plugin, "Settings");

        Set<String> key;
        try {
            key = config.getConfigurationSection("Spectator").getKeys(false);
        } catch (Exception ex) {
            key = null;
        }
        if (key == null | key.size() < 1) return;
        Player p = (Player) e.getWhoClicked();

        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            for (String item : key) {
                String path = "Spectator." + item + ".";
                String _item = config.getString(path + "item");
                String _name = config.getString(path + "name");
                List<String> _lore = config.getStringList(path + "lore");
                String _action = config.getString(path + "action");
                ItemStack itemStack = ItemBuilder.crearItem1(Integer.valueOf(_item.split(":")[0]), 1, Integer.valueOf(_item.split(":")[1]), _name, _lore);

                if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

                if (e.getCurrentItem().equals(itemStack)) {
                    if (_action.split(":")[0].equalsIgnoreCase("open")) {
                        new MenuCreator(p, plugin, _action.split(":")[1], arena).o(p);
                    } else if (_action.split(":")[0].equalsIgnoreCase("cmd")) {
                        String cmd = "/" + _action.split(":")[1];
                        p.chat(cmd);
                    } else if (_action.split(":")[0].equalsIgnoreCase("leave")) {
                        if (settings.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.INTERACT);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        } else if (settings.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.BUNGEE);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        }

                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void SpectatorItemsInteract(PlayerInteractEvent e) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/Inventory");
        Configuration settings = plugin.getConfigUtils().getConfig(plugin, "Settings");

        Set<String> key;
        try {
            key = config.getConfigurationSection("Spectator").getKeys(false);
        } catch (Exception ex) {
            key = null;
        }
        if (key == null | key.size() < 1) return;
        Player p = e.getPlayer();
        if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
            Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
            for (String item : key) {
                String path = "Spectator." + item + ".";
                String _item = config.getString(path + "item");
                String _name = config.getString(path + "name");
                List<String> _lore = config.getStringList(path + "lore");
                String _action = config.getString(path + "action");
                ItemStack itemStack = ItemBuilder.crearItem1(Integer.valueOf(_item.split(":")[0]), 1, Integer.valueOf(_item.split(":")[1]), _name, _lore);

                if (e.getItem() == null || e.getItem().getItemMeta().equals(null)) return;

                if (e.getItem().equals(itemStack)) {
                    if (_action.split(":")[0].equalsIgnoreCase("open")) {
                        new MenuCreator(e.getPlayer(), plugin, _action.split(":")[1], arena).o(e.getPlayer());
                    } else if (_action.split(":")[0].equalsIgnoreCase("cmd")) {
                        String cmd = "/" + _action.split(":")[1];
                        e.getPlayer().chat(cmd);
                    } else if (_action.split(":")[0].equalsIgnoreCase("leave")) {
                        if (settings.getString("Mode").equalsIgnoreCase("MULTIARENA")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.INTERACT);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        } else if (settings.getString("Mode").equalsIgnoreCase("BUNGEE")) {
                            LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.BUNGEE);
                            Bukkit.getPluginManager().callEvent(leftEvent);
                        }
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

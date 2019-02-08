package com.isnakebuzz.survivalgames.Cmds;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Listeners.Custom.LeftEvent;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {

    private Main plugin;

    public LeaveCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("leave") && sender instanceof Player) {
            Player p = (Player) sender;
            if (plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                Arena arena = plugin.getArenaManager().getArenaPlayer().get(p.getUniqueId());
                LeftEvent leftEvent = new LeftEvent(p, arena, Enums.LeftCause.COMMAND);
                Bukkit.getPluginManager().callEvent(leftEvent);
            }
        }

        return false;
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}

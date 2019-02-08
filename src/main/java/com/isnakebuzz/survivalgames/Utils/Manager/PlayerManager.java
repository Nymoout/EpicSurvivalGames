package com.isnakebuzz.survivalgames.Utils.Manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Player.GamePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    private Main plugin;
    private HashMap<UUID, GamePlayer> gamePlayers;

    public PlayerManager(Main plugin) {
        this.plugin = plugin;
        this.gamePlayers = new HashMap<>();
    }


    public void addPlayer(Player p, GamePlayer gamePlayer) {
        if (!this.containsPlayer(p)) {
            this.gamePlayers.put(p.getUniqueId(), gamePlayer);
        }
    }

    public void removePlayer(Player p) {
        if (this.gamePlayers.containsKey(p.getUniqueId())) {
            this.gamePlayers.remove(p.getUniqueId());
        }
    }

    public boolean containsPlayer(Player p) {
        return this.gamePlayers.containsKey(p.getUniqueId());
    }

    public GamePlayer getPlayer(Player p) {
        if (this.containsPlayer(p)) {
            return this.gamePlayers.get(p.getUniqueId());
        } else {
            return null;
        }
    }

    public void sendLobby(Player p) {
        Configuration settings = plugin.getConfigUtils().getConfig(plugin, "Settings");
        plugin.getPlayerManager().connect(p, settings.getString("Bungee.Lobby"));
    }

    public void connect(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

}

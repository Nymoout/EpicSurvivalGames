package com.isnakebuzz.survivalgames.Listeners.Custom;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WinEvent extends Event {

    private static HandlerList handlerList;

    static {
        handlerList = new HandlerList();
    }

    private Player player;
    private Arena arena;

    public WinEvent(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }

    public static HandlerList getHandlerList() {
        return WinEvent.handlerList;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return WinEvent.handlerList;
    }
}

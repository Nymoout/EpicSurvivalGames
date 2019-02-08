package com.isnakebuzz.survivalgames.Listeners.Custom;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class Example extends Event {

    private static HandlerList handlerList;
    private Player player;
    private Arena arena;


    public Example(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return Example.handlerList;
    }

    public static HandlerList getHandlerList() {
        return Example.handlerList;
    }

    static {
        handlerList = new HandlerList();
    }
}

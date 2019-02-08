package com.isnakebuzz.survivalgames.Listeners.Custom;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StartEvent extends Event {

    private static HandlerList handlerList;

    static {
        handlerList = new HandlerList();
    }

    private Arena arena;

    public StartEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return StartEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return StartEvent.handlerList;
    }

    public Arena getArena() {
        return arena;
    }
}

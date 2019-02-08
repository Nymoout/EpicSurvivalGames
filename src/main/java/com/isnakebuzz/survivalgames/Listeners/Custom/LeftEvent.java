package com.isnakebuzz.survivalgames.Listeners.Custom;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeftEvent extends Event {

    private static HandlerList handlerList;

    static {
        handlerList = new HandlerList();
    }

    private Player player;
    private Arena arena;
    private Enums.LeftCause leftCause;

    public LeftEvent(Player player, Arena arena, Enums.LeftCause leftCause) {
        this.player = player;
        this.arena = arena;
        this.leftCause = leftCause;
    }

    @Override
    public HandlerList getHandlers() {
        return LeftEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return LeftEvent.handlerList;
    }

    public Enums.LeftCause getLeftCause() {
        return leftCause;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }
}

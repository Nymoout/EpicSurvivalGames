package com.isnakebuzz.survivalgames.Listeners.Custom;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JoinEvent extends Event {

    private static HandlerList handlerList;

    static {
        handlerList = new HandlerList();
    }

    private Player player;
    private Arena arena;
    private Enums.JoinCause joinCause;

    public JoinEvent(Player player, Arena arena, Enums.JoinCause joinCause) {
        this.player = player;
        this.arena = arena;
        this.joinCause = joinCause;
    }

    public static HandlerList getHandlerList() {
        return JoinEvent.handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return JoinEvent.handlerList;
    }

    public Enums.JoinCause getJoinCause() {
        return joinCause;
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }
}

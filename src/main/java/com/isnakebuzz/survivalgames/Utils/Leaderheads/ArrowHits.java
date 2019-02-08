package com.isnakebuzz.survivalgames.Utils.Leaderheads;

import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Player.GamePlayer;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ArrowHits extends OnlineDataCollector {

    private Main plugin;

    public ArrowHits(Main plugin, String top) {
        super("esg-" + top, "EpicSurvivalGames", BoardType.DEFAULT, "SG - Top " + top, "Sg" + top, Arrays.asList(null, "&9{name}", "&6{amount}", null));
        this.plugin = plugin;
    }

    public Double getScore(final Player p) {
        GamePlayer playerStats = plugin.getPlayerManager().getPlayer(p);
        return (double) playerStats.getAHits();
    }
}

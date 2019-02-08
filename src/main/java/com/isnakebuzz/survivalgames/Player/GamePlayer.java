package com.isnakebuzz.survivalgames.Player;

import org.bukkit.entity.Player;

public class GamePlayer {

    private Player player;

    //Stats
    private int Wins;
    private int Kills;
    private int Deaths;
    private int GPlayeds;
    private int AShots;
    private int AHits;

    //Internal stats
    private int KillStreak;

    public GamePlayer(Player player, int wins, int kills, int deaths, int gPlayeds, int aShots, int aHits) {
        this.player = player;
        this.Wins = wins;
        this.Kills = kills;
        this.Deaths = deaths;
        this.GPlayeds = gPlayeds;
        this.AShots = aShots;
        this.AHits = aHits;

        this.KillStreak = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWins() {
        return Wins;
    }

    public void setWins(int wins) {
        Wins = wins;
    }

    public int getKills() {
        return Kills;
    }

    public void setKills(int kills) {
        Kills = kills;
    }

    public int getDeaths() {
        return Deaths;
    }

    public void setDeaths(int deaths) {
        Deaths = deaths;
    }

    public int getAShots() {
        return AShots;
    }

    public void setAShots(int AShots) {
        this.AShots = AShots;
    }

    public int getAHits() {
        return AHits;
    }

    public void setAHits(int AHits) {
        this.AHits = AHits;
    }

    public int getGPlayeds() {
        return GPlayeds;
    }

    public void setGPlayeds(int GPlayeds) {
        this.GPlayeds = GPlayeds;
    }

    public void addWins() {
        setWins(this.getWins() + 1);
    }

    public void setKillStreak(int killStreak) {
        KillStreak = killStreak;
    }

    public int getKillStreak() {
        return KillStreak;
    }

    public void addKills() {
        setKills(this.getKills() + 1);
    }

    public void addKt() {
        setKillStreak(this.getKillStreak() + 1);
    }

    public void addDeaths() {
        setDeaths(this.getDeaths() + 1);
    }

    public void addGPlayed() {
        setGPlayeds(this.getGPlayeds() + 1);
    }

    public void addAShots() {
        setAShots(this.getAShots() + 1);
    }

    public void addAHits() {
        setAHits(this.getAHits() + 1);
    }

}

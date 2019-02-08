package com.isnakebuzz.survivalgames.Utils;

public class Enums {

    public enum GameStatus {

        LOADING,
        WAITING,
        STARTING,
        INGAME,
        RESTARTING

    }

    public enum JoinCause {
        SIGN,
        COMMAND,
        BUNGEE
    }

    public enum LeftCause {
        COMMAND,
        INTERACT,
        BUNGEE
    }

    public enum ChestType {
        BASIC,
        NORMAL,
        OVERPOWERED,
        NOBODY
    }

    public enum TimeType {
        DAY,
        SUNSET,
        NIGHT,
        NOBODY
    }

    public enum HealthType {
        HARD,
        NORMAL,
        DOUBLE,
        TRIPLE,
        NOBODY
    }

    public enum VoteType {
        CHEST,
        TIME,
        HEALTH
    }
}

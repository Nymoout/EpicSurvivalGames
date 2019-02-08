package com.isnakebuzz.survivalgames.ArenaUtils;

import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaVoteSys {

    private Arena arena;

    //Chest types
    private List<Player> basicChests;
    private List<Player> normalChests;
    private List<Player> opChests;

    //Time types
    private List<Player> dayTime;
    private List<Player> sunsetTime;
    private List<Player> nightTime;

    //Time types
    private List<Player> halfLife;
    private List<Player> normalLife;
    private List<Player> doubleLife;
    private List<Player> tripleLife;


    public ArenaVoteSys(Arena arena) {
        this.arena = arena;

        //Chest types
        this.basicChests = new ArrayList<>();
        this.normalChests = new ArrayList<>();
        this.opChests = new ArrayList<>();

        //Chest types
        this.dayTime = new ArrayList<>();
        this.sunsetTime = new ArrayList<>();
        this.nightTime = new ArrayList<>();

        //Health types
        this.halfLife = new ArrayList<>();
        this.normalLife = new ArrayList<>();
        this.doubleLife = new ArrayList<>();
        this.tripleLife = new ArrayList<>();
    }


    private void removeVotes(Player p, Enums.VoteType voteType) {
        if (voteType.equals(Enums.VoteType.CHEST)) {
            if (this.basicChests.contains(p)) {
                this.basicChests.remove(p);
            }
            if (this.normalChests.contains(p)) {
                this.normalChests.remove(p);
            }
            if (this.opChests.contains(p)) {
                this.opChests.remove(p);
            }
        } else if (voteType.equals(Enums.VoteType.TIME)) {
            if (this.dayTime.contains(p)) {
                this.dayTime.remove(p);
            }
            if (this.sunsetTime.contains(p)) {
                this.sunsetTime.remove(p);
            }
            if (this.nightTime.contains(p)) {
                this.nightTime.remove(p);
            }
        } else if (voteType.equals(Enums.VoteType.HEALTH)) {
            if (this.halfLife.contains(p)) {
                this.halfLife.remove(p);
            }
            if (this.normalLife.contains(p)) {
                this.normalLife.remove(p);
            }
            if (this.doubleLife.contains(p)) {
                this.doubleLife.remove(p);
            }
            if (this.tripleLife.contains(p)) {
                this.tripleLife.remove(p);
            }
        }
    }

    public void addVote(Player p, Enums.VoteType voteType, Enums.ChestType chestType, Enums.HealthType healthType, Enums.TimeType timeType) {
        removeVotes(p, voteType);
        if (voteType.equals(Enums.VoteType.CHEST)) {
            if (chestType.equals(Enums.ChestType.BASIC)) {
                this.basicChests.add(p);
            } else if (chestType.equals(Enums.ChestType.NORMAL)) {
                this.normalChests.add(p);
            } else if (chestType.equals(Enums.ChestType.OVERPOWERED)) {
                this.opChests.add(p);
            }
        } else if (voteType.equals(Enums.VoteType.TIME)) {
            if (timeType.equals(Enums.TimeType.DAY)) {
                this.dayTime.add(p);
            } else if (timeType.equals(Enums.TimeType.SUNSET)) {
                this.sunsetTime.add(p);
            } else if (timeType.equals(Enums.TimeType.NIGHT)) {
                this.nightTime.add(p);
            }
        } else if (voteType.equals(Enums.VoteType.HEALTH)) {
            if (healthType.equals(Enums.HealthType.HARD)) {
                this.halfLife.add(p);
            } else if (healthType.equals(Enums.HealthType.NORMAL)) {
                this.normalLife.add(p);
            } else if (healthType.equals(Enums.HealthType.DOUBLE)) {
                this.doubleLife.add(p);
            } else if (healthType.equals(Enums.HealthType.TRIPLE)) {
                this.tripleLife.add(p);
            }
        }
    }


    public void setTypes() {
        checkChests();
        checkHealth();
        checkTime();
    }

    private void checkHealth() {
        if (this.halfLife.size() > this.normalLife.size() && this.halfLife.size() > this.doubleLife.size() && this.halfLife.size() > this.tripleLife.size()) {
            arena.setHealthType(Enums.HealthType.HARD);
        } else if (this.normalLife.size() > this.halfLife.size() && this.normalLife.size() > this.doubleLife.size() && this.normalLife.size() > this.tripleLife.size()) {
            arena.setHealthType(Enums.HealthType.NORMAL);
        } else if (this.doubleLife.size() > this.halfLife.size() && this.doubleLife.size() > this.normalLife.size() && this.doubleLife.size() > this.tripleLife.size()) {
            arena.setHealthType(Enums.HealthType.DOUBLE);
        } else if (this.tripleLife.size() > this.halfLife.size() && this.tripleLife.size() > this.normalLife.size() && this.tripleLife.size() > this.doubleLife.size()) {
            arena.setHealthType(Enums.HealthType.TRIPLE);
        } else {
            arena.setHealthType(Enums.HealthType.NOBODY);
        }
    }

    private void checkTime() {
        if (this.dayTime.size() > this.sunsetTime.size() && this.dayTime.size() > this.nightTime.size()) {
            arena.setTimeType(Enums.TimeType.DAY);
        } else if (this.sunsetTime.size() > this.nightTime.size() && this.sunsetTime.size() > this.dayTime.size()) {
            arena.setTimeType(Enums.TimeType.SUNSET);
        } else if (this.nightTime.size() > this.dayTime.size() && this.nightTime.size() > this.sunsetTime.size()) {
            arena.setTimeType(Enums.TimeType.NIGHT);
        } else {
            arena.setTimeType(Enums.TimeType.NOBODY);
        }
    }

    private void checkChests() {
        if (this.basicChests.size() > this.normalChests.size() && this.basicChests.size() > this.opChests.size()) {
            arena.setChestType(Enums.ChestType.BASIC);
        } else if (this.normalChests.size() > this.basicChests.size() && this.normalChests.size() > this.opChests.size()) {
            arena.setChestType(Enums.ChestType.NORMAL);
        } else if (this.opChests.size() > this.normalChests.size() && this.opChests.size() > this.basicChests.size()) {
            arena.setChestType(Enums.ChestType.OVERPOWERED);
        } else {
            arena.setChestType(Enums.ChestType.NOBODY);
        }
    }

    public int getVotes(Enums.ChestType chestType) {
        if (chestType.equals(Enums.ChestType.BASIC)) {
            return this.basicChests.size();
        } else if (chestType.equals(Enums.ChestType.NORMAL)) {
            return this.normalChests.size();
        } else if (chestType.equals(Enums.ChestType.OVERPOWERED)) {
            return this.opChests.size();
        } else {
            return 777;
        }
    }

    public int getVotes(Enums.TimeType timeType) {
        if (timeType.equals(Enums.TimeType.DAY)) {
            return this.dayTime.size();
        } else if (timeType.equals(Enums.TimeType.SUNSET)) {
            return this.sunsetTime.size();
        } else if (timeType.equals(Enums.TimeType.NIGHT)) {
            return this.nightTime.size();
        } else {
            return 777;
        }
    }

    public boolean containsVote(Player p, Enums.VoteType voteType) {
        if (voteType.equals(Enums.VoteType.CHEST)) {
            if (this.basicChests.contains(p)) {
                return true;
            } else if (this.normalChests.contains(p)) {
                return true;
            } else if (this.opChests.contains(p)) {
                return true;
            } else {
                return false;
            }
        } else if (voteType.equals(Enums.VoteType.TIME)) {
            if (this.dayTime.contains(p)) {
                return true;
            } else if (this.sunsetTime.contains(p)) {
                return true;
            } else if (this.nightTime.contains(p)) {
                return true;
            } else {
                return false;
            }
        } else if (voteType.equals(Enums.VoteType.HEALTH)) {
            if (this.halfLife.contains(p)) {
                return true;
            } else if (this.normalLife.contains(p)) {
                return true;
            } else if (this.doubleLife.contains(p)) {
                return true;
            } else if (this.tripleLife.contains(p)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void removeFromVotes(Player p) {
        if (this.basicChests.contains(p)) {
            this.basicChests.remove(p);
        }
        if (this.normalChests.contains(p)) {
            this.normalChests.remove(p);
        }
        if (this.opChests.contains(p)) {
            this.opChests.remove(p);
        }
        if (this.dayTime.contains(p)) {
            this.dayTime.remove(p);
        }
        if (this.sunsetTime.contains(p)) {
            this.sunsetTime.remove(p);
        }
        if (this.nightTime.contains(p)) {
            this.nightTime.remove(p);
        }
        if (this.halfLife.contains(p)) {
            this.halfLife.remove(p);
        }
        if (this.normalLife.contains(p)) {
            this.normalLife.remove(p);
        }
        if (this.doubleLife.contains(p)) {
            this.doubleLife.remove(p);
        }
        if (this.tripleLife.contains(p)) {
            this.tripleLife.remove(p);
        }
    }

    public int getVotes(Enums.HealthType healthType) {
        if (healthType.equals(Enums.HealthType.HARD)) {
            return this.halfLife.size();
        } else if (healthType.equals(Enums.HealthType.NORMAL)) {
            return this.normalLife.size();
        } else if (healthType.equals(Enums.HealthType.DOUBLE)) {
            return this.doubleLife.size();
        } else if (healthType.equals(Enums.HealthType.TRIPLE)) {
            return this.tripleLife.size();
        } else {
            return 777;
        }
    }

    public int getVotes(Enums.VoteType voteType) {
        if (voteType.equals(Enums.VoteType.HEALTH)) {
            return this.halfLife.size() + this.normalLife.size() + this.doubleLife.size() + this.tripleLife.size();
        } else if (voteType.equals(Enums.VoteType.CHEST)) {
            return this.normalChests.size() + this.opChests.size() + this.basicChests.size();
        } else if (voteType.equals(Enums.VoteType.TIME)) {
            return this.nightTime.size() + this.sunsetTime.size() + this.dayTime.size();
        } else {
            return 777;
        }
    }

}

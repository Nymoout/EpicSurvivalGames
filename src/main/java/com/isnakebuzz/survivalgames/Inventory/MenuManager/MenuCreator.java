package com.isnakebuzz.survivalgames.Inventory.MenuManager;

import com.isnakebuzz.survivalgames.ArenaUtils.Arena;
import com.isnakebuzz.survivalgames.Inventory.Utils.ItemBuilder;
import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Enums;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MenuCreator extends Menu {

    private Main plugin;
    private Arena arena;
    private int taskId;

    public MenuCreator(Player player, Main plugin, String _name, Arena arena) {
        super(plugin, _name);
        this.plugin = plugin;
        this.arena = arena;
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/MenuCreator");
        String path = "MenuCreator." + _name + ".";
        if (config.getBoolean(path + "update.enabled")) {
            taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
                updateInv(player, _name);
            }, 0, config.getInt(path + "update.time"));
        } else {
            this.updateInv(player, _name);
        }
    }

    @Override
    public void onClick(final Player p, final ItemStack itemStack, String _name) {
        Configuration lang = plugin.getConfigUtils().getConfig(plugin, "Lang");
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/MenuCreator");
        String menu_path = "MenuCreator." + _name + ".";
        Set<String> key = config.getConfigurationSection(menu_path + "items").getKeys(false);
        for (String _item : key) {
            String path = menu_path + "items." + _item + ".";
            String item = config.getString(path + "item");
            int amount = config.getInt(path + "amount");
            String name = config.getString(path + "name");
            List<String> lore = chars(config.getStringList(path + "lore"));
            String permission = config.getString(path + "perms");
            String action = config.getString(path + "action");
            ItemStack itemStack1 = ItemBuilder.crearItem1(arena, Integer.valueOf(item.split(":")[0]), amount, Integer.valueOf(item.split(":")[1]), name, lore);
            if (itemStack.equals(itemStack1)) {
                if (!p.hasPermission(permission) && !permission.equalsIgnoreCase("none")) {
                    p.sendMessage(c(lang.getString("NoPerms")));
                    return;
                }
                if (action.split(":")[0].equalsIgnoreCase("open")) {
                    new MenuCreator(p, plugin, action.split(":")[1], arena).o(p);
                } else if (action.split(":")[0].equalsIgnoreCase("cmd")) {
                    String cmd = "/" + action.split(":")[1];
                    p.chat(cmd);
                } else if (action.split(":")[0].equalsIgnoreCase("velocity")) {
                    float speed = Float.valueOf(action.split(":")[1]);
                    p.setWalkSpeed(speed);
                } else if (action.split(":")[0].equalsIgnoreCase("flyspeed")) {
                    float speed = Float.valueOf(action.split(":")[1]);
                    p.setFlySpeed(speed);
                } else if (action.split(":")[0].equalsIgnoreCase("vote")) {
                    if (!plugin.getArenaManager().getArenaPlayer().containsKey(p.getUniqueId())) {
                        return;
                    }


                    String vote = action.split(":")[1];

                    //Chest types
                    if (vote.equalsIgnoreCase("basicChest")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.CHEST)) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.CHEST, Enums.ChestType.BASIC, null, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Chests." + Enums.ChestType.BASIC.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.ChestType.BASIC)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.CHEST.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("normalChest")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.CHEST)) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.CHEST, Enums.ChestType.NORMAL, null, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Chests." + Enums.ChestType.NORMAL.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.ChestType.NORMAL)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.CHEST.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("opChest")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.CHEST)) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.CHEST, Enums.ChestType.OVERPOWERED, null, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Chests." + Enums.ChestType.OVERPOWERED.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.ChestType.OVERPOWERED)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.CHEST.toString()))
                            ));
                        }
                    }

                    //Time Types
                    if (vote.equalsIgnoreCase("dayTime")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.TIME) && vote.contains("Time")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.TIME, null, null, Enums.TimeType.DAY);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Time." + Enums.TimeType.DAY.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.TimeType.DAY)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.TIME.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("sunsetTime")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.TIME) && vote.contains("Time")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.TIME, null, null, Enums.TimeType.SUNSET);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Time." + Enums.TimeType.SUNSET.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.TimeType.SUNSET)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.TIME.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("nightTime")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.TIME) && vote.contains("Time")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.TIME, null, null, Enums.TimeType.NIGHT);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Time." + Enums.TimeType.NIGHT.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.TimeType.NIGHT)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.TIME.toString()))
                            ));
                        }
                    }

                    //Health Types
                    if (vote.equalsIgnoreCase("hardHealth")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.HEALTH) && vote.contains("Health")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.HEALTH, null, Enums.HealthType.HARD, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Health." + Enums.HealthType.HARD.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.HARD)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types" + Enums.VoteType.HEALTH.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("normalHealth")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.HEALTH) && vote.contains("Health")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.HEALTH, null, Enums.HealthType.NORMAL, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Health." + Enums.HealthType.NORMAL.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.NOBODY)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.HEALTH.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("doubleHealth")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.HEALTH) && vote.contains("Health")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.HEALTH, null, Enums.HealthType.DOUBLE, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Health." + Enums.HealthType.DOUBLE.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.DOUBLE)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.HEALTH.toString()))
                            ));
                        }
                    } else if (vote.equalsIgnoreCase("tripleHealth")) {
                        if (!arena.getArenaVoteSys().containsVote(p, Enums.VoteType.HEALTH) && vote.contains("Health")) {
                            arena.getArenaVoteSys().addVote(p, Enums.VoteType.HEALTH, null, Enums.HealthType.TRIPLE, null);

                            arena.broadcast(lang.getString("VoteBC.Message")
                                    .replaceAll("%player%", p.getName())
                                    .replaceAll("%type%", lang.getString("VoteBC.Types.Health." + Enums.HealthType.TRIPLE.toString()))
                                    .replaceFirst("%votes%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.TRIPLE)))
                            );
                        } else {
                            p.sendMessage(c(lang.getString("VoteBC.Already")
                                    .replaceAll("%type%", lang.getString("VoteBC.Types." + Enums.VoteType.HEALTH.toString()))
                            ));
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onClose(Player p) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    private void updateInv(Player p, String _name) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Utils/MenuCreator");
        String menu_path = "MenuCreator." + _name + ".";
        Set<String> key = config.getConfigurationSection(menu_path + "items").getKeys(false);
        for (String _item : key) {
            String path = menu_path + "items." + _item + ".";
            String item = config.getString(path + "item");
            int slot = config.getInt(path + "slot");
            int amount = config.getInt(path + "amount");
            String name = config.getString(path + "name");
            List<String> lore = chars(config.getStringList(path + "lore"));
            String action = config.getString(path + "action");
            ItemStack itemStack = ItemBuilder.crearItem1(arena, Integer.valueOf(item.split(":")[0]), amount, Integer.valueOf(item.split(":")[1]), name, lore);
            this.s(slot, itemStack);
        }
    }

    private List<String> chars(List<String> messages) {
        List<String> newMessages = new ArrayList<>();
        for (String msg : messages) {
            newMessages.add(msg
                    //Principal holders
                    .replaceAll("%votes_chests%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.VoteType.CHEST)))
                    .replaceAll("%votes_times%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.VoteType.TIME)))
                    .replaceAll("%votes_health%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.VoteType.HEALTH)))

                    //Chests holders
                    .replaceAll("%votes_basicChest%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.ChestType.BASIC)))
                    .replaceAll("%votes_normalChest%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.ChestType.NORMAL)))
                    .replaceAll("%votes_opChest%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.ChestType.OVERPOWERED)))

                    //Time holders
                    .replaceAll("%votes_dayTime%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.TimeType.DAY)))
                    .replaceAll("%votes_sunsetTime%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.TimeType.SUNSET)))
                    .replaceAll("%votes_nightTime%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.TimeType.NIGHT)))

                    //Health holders
                    .replaceAll("%votes_hardHealth%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.HARD)))
                    .replaceAll("%votes_normalHealth%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.NORMAL)))
                    .replaceAll("%votes_doubleHealth%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.DOUBLE)))
                    .replaceAll("%votes_tripleHealth%", String.valueOf(arena.getArenaVoteSys().getVotes(Enums.HealthType.TRIPLE)))
            );
        }
        return newMessages;
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}

package com.isnakebuzz.survivalgames.Player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerUtils {

    public static void clean(Player p, GameMode gameMode, boolean allowflight, boolean flying, boolean vanished) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setGameMode(gameMode);
        p.setMaxHealth(20);
        p.setFoodLevel(20);
        p.setExhaustion(20);
        p.setHealth(20);
        p.setAllowFlight(allowflight);
        p.setFlying(flying);
        p.setLevel(0);
        p.setExp(0);
        p.setVelocity(new Vector(0, 0, 0).normalize());
        p.getInventory().setHeldItemSlot(4);
        p.getActivePotionEffects().forEach(potionEffect -> {
            p.removePotionEffect(potionEffect.getType());
        });
        if (vanished)
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
    }

}

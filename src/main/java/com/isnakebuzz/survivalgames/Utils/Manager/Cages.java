package com.isnakebuzz.survivalgames.Utils.Manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Cages {

    public static void cage(Location Plocation, Material material) {
        final int x = Plocation.getBlockX();
        final int y = Plocation.getBlockY();
        final int z = Plocation.getBlockZ();
        final Block yap = Plocation.getWorld().getBlockAt(x, y + 2, z);
        final Block xap = Plocation.getWorld().getBlockAt(x + 1, y, z);
        final Block zap = Plocation.getWorld().getBlockAt(x, y, z + 1);
        final Block xan = Plocation.getWorld().getBlockAt(x - 1, y, z);
        final Block zan = Plocation.getWorld().getBlockAt(x, y, z - 1);
        yap.setType(material);
        xap.setType(material);
        zap.setType(material);
        xan.setType(material);
        zan.setType(material);
    }

}

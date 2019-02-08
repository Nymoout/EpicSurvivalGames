package com.isnakebuzz.survivalgames.Signs.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil
{
    public static Location Center(final Location location) {
        return new Location(location.getWorld(), getRelativeCoord(location.getBlockX()), getRelativeCoord(location.getBlockY()), getRelativeCoord(location.getBlockZ()));
    }
    
    private static double getRelativeCoord(final int n) {
        final double n2 = n;
        return (n2 < 0.0) ? (n2 + 0.5) : (n2 + 0.5);
    }
    
    public static String getString(final Location location, final boolean b) {
        if (b) {
            return location.getWorld().getName() + "," + Center(location).getX() + "," + location.getY() + "," + Center(location).getZ() + "," + 0 + "," + location.getYaw();
        }
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getPitch() + "," + location.getYaw();
    }
    
    public static Location getLocation(final String s) {
        final String[] split = s.split(",");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[5]), Float.parseFloat("0"));
    }
}

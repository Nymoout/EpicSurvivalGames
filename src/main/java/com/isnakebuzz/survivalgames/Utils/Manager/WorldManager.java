package com.isnakebuzz.survivalgames.Utils.Manager;

import com.isnakebuzz.survivalgames.Main;
import com.isnakebuzz.survivalgames.Utils.Callback;
import com.isnakebuzz.survivalgames.Utils.EmptyChunk;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.io.*;
import java.util.List;

public class WorldManager {

    private Main plugin;
    private String backPath;
    private String worldPath;

    public WorldManager(Main plugin) {
        this.plugin = plugin;
        this.backPath = plugin.getServer().getWorldContainer().getPath() + "/Maps/";
        this.worldPath = plugin.getServer().getWorldContainer().getPath() + "/";
        if (!new File(backPath).exists()) {
            new File(backPath).mkdir();
        }
    }

    public void resetArena(List<Block> list, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Long currentTimeMillis = System.currentTimeMillis();
            int i = 0;
            String worldname = "error " + list.size();
            for (Block b : list) {
                if (i == 0) worldname = b.getWorld().getName();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    b.setType(Material.AIR);
                });
                i++;
            }
            String finalWorldname = worldname;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                plugin.log("SurvivalGames", "Has been reseted &a" + finalWorldname + "&e in &c" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
                callback.done(true);
            });
        });
    }

    public void resetWorldV2(String world, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Long currentTimeMillis = System.currentTimeMillis();
            arenaClone(new File(backPath + world), world);
            WorldCreator worldCreator = new WorldCreator(world);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                worldCreator.generateStructures(false);
                plugin.log("SurvivalGames", "Has been reseted &a" + world + "&e in &c" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
                callback.done(true);
            });
        });
    }

    public void resetWorld(String world, Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Long currentTimeMillis = System.currentTimeMillis();

            Bukkit.unloadWorld(world, false);

            try {
                File worldDir = new File(worldPath + world);
                FileUtils.deleteDirectory(worldDir);
                if (!worldDir.exists()) {
                    worldDir.mkdirs();
                }

                FileUtils.copyDirectory(new File(backPath + world), worldDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                WorldCreator worldc = new WorldCreator(world);
                worldc.generator(new EmptyChunk());
                worldc.createWorld();
                plugin.log("SurvivalGames", "Has been reseted &a" + world + "&e in &c" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
                callback.done(true);
            });
        });
    }

    public void saveWorld(String world) throws IOException {
        File worldDir = new File(backPath + world);
        if (!worldDir.exists()) {
            worldDir.mkdirs();
        }
        Bukkit.getWorld(world).save();
        FileUtils.copyDirectory(new File(plugin.getServer().getWorldContainer() + "/" + world), worldDir);
    }

    private void arenaClone(final File file, final String s) {
        try {
            delete(new File(file, "uid.dat"));
            delete(new File(file, "session.lock"));
            delete(new File(s));
            copyFolder(file, new File(s));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final World world = Bukkit.getWorld(s);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }
    }

    private void copyFolder(final File file, final File file2) {
        if (file.isDirectory()) {
            if (!file2.exists()) {
                file2.mkdir();
            }
            for (final String s : file.list()) {
                copyFolder(new File(file, s), new File(file2, s));
            }
        } else {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                byte[] array = new byte[1024];
                int read;
                while ((read = fileInputStream.read(array)) > 0) {
                    fileOutputStream.write(array, 0, read);
                }
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void delete(final File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                final String[] list = file.list();
                for (int length = list.length, i = 0; i < length; ++i) {
                    delete(new File(file, list[i]));
                }
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else if (file.exists()) {
            file.delete();
        }
    }

}

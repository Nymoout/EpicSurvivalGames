package com.isnakebuzz.survivalgames.Utils.Manager;

import com.isnakebuzz.survivalgames.Main;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileManager {

    private Main plugin;

    public FileManager(Main plugin) {
        this.plugin = plugin;
    }

    public void copyFile(File file, File file2) {
        try {
            if (file.isDirectory()) {
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                String[] list;
                for (int length = (list = file.list()).length, i = 0; i < length; ++i) {
                    final String s = list[i];
                    this.copyFile(new File(file, s), new File(file2, s));
                }
            } else {
                final FileInputStream fileInputStream = new FileInputStream(file);
                final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                final FileChannel channel = fileInputStream.getChannel();
                final FileChannel channel2 = fileOutputStream.getChannel();
                try {
                    channel.transferTo(0L, channel.size(), channel2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                } finally {
                    if (channel != null) {
                        channel.close();
                    }
                    if (channel2 != null) {
                        channel2.close();
                    }
                    fileInputStream.close();
                    fileOutputStream.close();
                }
                if (channel != null) {
                    channel.close();
                }
                if (channel2 != null) {
                    channel2.close();
                }
                fileInputStream.close();
                fileOutputStream.close();
            }
        } catch (IOException ex2) {
            plugin.log("EpicSurvivalGames", "Failed copy and paste a world!");
            ex2.printStackTrace();
        }
    }

}

package by.vengdevs.vchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    public static final Logger instance = new Logger();

    public void sendConsole(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}

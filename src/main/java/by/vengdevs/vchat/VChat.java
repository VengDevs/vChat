package by.vengdevs.vchat;

import by.vengdevs.vchat.commands.Chat;
import by.vengdevs.vchat.handlers.PlayerChat;
import by.vengdevs.vchat.handlers.PlayerJoin;
import by.vengdevs.vchat.utils.Emoji;
import by.vengdevs.vchat.utils.Logger;
import by.vengdevs.vchat.utils.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class VChat extends JavaPlugin {

    public static VChat instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveConfig();

        Emoji.loadEmojis();

        registerHandlers();
        registerCommands();

        Updater.startUpdateScheduler();

        Logger.instance.sendConsole("&5[vChat]&r &aSuccessfully enabled!");
    }

    @Override
    public void onDisable() {
        Logger.instance.sendConsole("&5[vChat]&r &aSuccessfully disabled!");
    }

    private void registerHandlers() {
        Logger.instance.sendConsole("&5[vChat]&r &eRegistering handlers...");

        getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);

        Logger.instance.sendConsole("&5[vChat]&r &aRegistered handlers!");
    }

    private void registerCommands() {
        Logger.instance.sendConsole("&5[vChat]&r &eRegistering commands...");

        Objects.requireNonNull(getCommand("chat")).setExecutor(new Chat());
        Objects.requireNonNull(getCommand("chat")).setTabCompleter(new Chat());

        Logger.instance.sendConsole("&5[vChat]&r &aRegistered commands!");
    }
}

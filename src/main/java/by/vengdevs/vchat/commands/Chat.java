package by.vengdevs.vchat.commands;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.utils.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Chat implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> help_command_messages = new ArrayList<>();
        help_command_messages.add(ChatColor.translateAlternateColorCodes('&', "&bvChat:"));
        help_command_messages.add(ChatColor.translateAlternateColorCodes('&', "&7 - &b/chat info &7 - Информация"));
        help_command_messages.add(ChatColor.translateAlternateColorCodes('&', "&7 - &b/chat whatsnew &7 - Что нового в обновлении"));

        if (strings.length < 1) {
            for (String message : help_command_messages) {
                commandSender.sendMessage(message);
            }
            return true;
        }

        if (strings[0].equalsIgnoreCase("info")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bvChat"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Version: &b" + VChat.instance.getPluginMeta().getVersion()));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Developer: &eVengDevs"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Developer discord: &evengdevs"));
        } else if (strings[0].equalsIgnoreCase("whatsnew")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bТранслитерация сообщений и ответы | Version " + VChat.instance.getPluginMeta().getVersion()));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b - &fТеперь в конце Вашего сообщения есть кнопка \"Транслитерировать\", нажав на которую, все английские символы в Вашем сообщении заменятся на русские!"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b - &fНажав на имя отправителя сообщения в чате, можно ответить на него."));
        } else if (strings[0].equalsIgnoreCase("transliterate")) {
            char[] russianSymbols = "йцукенгшщзхъфывапролджэячсмитьбюё".toCharArray();
            char[] englishSymbols = "qwertyuiop[]asdfghjkl;'zxcvbnm,.`".toCharArray();

            Map<Character, Character> symbolMap = new HashMap<>();
            for (int i = 0; i < englishSymbols.length; i++) {
                symbolMap.put(englishSymbols[i], russianSymbols[i]);
            }

            if (commandSender instanceof Player) {
                StringBuilder message = new StringBuilder();
                for (int i = 1; i < strings.length; i++) {
                    StringBuilder word = new StringBuilder();

                    for (char symbol : strings[i].toCharArray()) {
                        char russianSymbol = symbolMap.getOrDefault(symbol, symbol);
                        word.append(russianSymbol);
                    }

                    message.append(word);
                    if (i != strings.length - 1) {
                        message.append(" ");
                    }
                }

                message.append(" ").append(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.pattern"))));
                MessageManager.instance.formatMessage(message.toString(), (Player) commandSender, true);
            }

        }
        else {
            for (String message : help_command_messages) {
                commandSender.sendMessage(message);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> subcommands = new ArrayList<>();
        if (strings.length == 1) {
            subcommands.add("info");
            subcommands.add("whatsnew");
        }
        return subcommands;
    }
}

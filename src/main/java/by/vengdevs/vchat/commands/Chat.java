package by.vengdevs.vchat.commands;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.managers.ChatManager;
import by.vengdevs.vchat.managers.MessageManager;
import by.vengdevs.vchat.utils.Color;
import by.vengdevs.vchat.utils.Emoji;
import org.bukkit.Sound;
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
        help_command_messages.add("&bДоступные команды:");
        help_command_messages.add("&7 - &b/chat info &7 - Информация");
        help_command_messages.add("&7 - &b/chat emojis &7 - Доступные эмоджи");
        help_command_messages.add("&7 - &b/chat whatsnew &7 - Что нового в обновлении");

        if (strings.length < 1) {
            for (String message : help_command_messages) {
                commandSender.sendMessage(Color.convert("&", message));
            }
            return true;
        }

        if (strings[0].equalsIgnoreCase("info")) {
            List<String> info = new ArrayList<>();
            info.add("&bvChat - многофункциональный плагин для чата");
            info.add("&fВерсия: &e" + VChat.instance.getPluginMeta().getVersion());
            info.add("&fРазработчик: &eVengDevs");
            info.add("&fДискорд разработчика: &evengdevs");
            info.add("&fИсходный код: &ehttps://github.com/VengDevs/vChat");
            for (String infoLine : info) {
                commandSender.sendMessage(Color.convert("&", infoLine));
            }
        } else if (strings[0].equalsIgnoreCase("whatsnew")) {
            List<String> whatsnew = new ArrayList<>();
            whatsnew.add("&bЭмоджи и улучшенная система транслитерации!");
            whatsnew.add("&f - Теперь написав :emoji-name: в своем сообщение, вы сможете использовать нужный вам эмоджи. Названия всех эмоджи можно узнать написав /chat emojis");
            whatsnew.add("&f - Транслитерировать определенное сообщение можно только один раз. Транслитерация предлагается только если в сообщении количество английских символов больше чем русских. Во время транслитерации эмоджи, упоминания и ответы не затрагиваются.");
            for (String whatsnewLine : whatsnew) {
                commandSender.sendMessage(Color.convert("&", whatsnewLine));
            }
        } else if (strings[0].equalsIgnoreCase("emojis")) {
            Map<String, String> emojis = Emoji.getAllEmojis();
            commandSender.sendMessage(Color.convert("&", "&bДоступные эмоджи:"));
            for (Map.Entry<String, String> pair : emojis.entrySet()) {
                commandSender.sendMessage(Color.convert("&", "&f - " + pair.getValue() + " &e:" + pair.getKey() + ":"));
            }
        } else if (strings[0].equalsIgnoreCase("reload")) {
            if (commandSender.hasPermission("vchat.commands.reload")) {
                Emoji.loadEmojis();
                VChat.instance.reloadConfig();
                commandSender.sendMessage(Color.convert("&", "&bКонфиг и эмоджи перезагружены!"));
            }
        } else if (strings[0].equalsIgnoreCase("transliterate")) {
            char[] russianSymbols = "йцукенгшщзхъфывапролджэячсмитьбюёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ".toCharArray();
            char[] englishSymbols = "qwertyuiop[]asdfghjkl;'zxcvbnm,.`QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>~".toCharArray();

            Map<Character, Character> symbolMap = new HashMap<>();
            for (int i = 0; i < englishSymbols.length; i++) {
                symbolMap.put(englishSymbols[i], russianSymbols[i]);
            }

            if (commandSender instanceof Player author) {
                StringBuilder transliteratedMessage = new StringBuilder();
                StringBuilder rawMessage = new StringBuilder();
                for (int i = 1; i < strings.length; i++) {
                    rawMessage.append(strings[i]);
                    if (i != strings.length - 1) {
                        rawMessage.append(" ");
                    }
                }

                boolean transliterated = false;
                HashMap<Player, String> transliteratedMessages = ChatManager.instance.getTransliteratedMessages();
                for (Map.Entry<Player, String> pair : transliteratedMessages.entrySet()) {
                    Player messageAuthor = pair.getKey();
                    String messageContent = pair.getValue();
                    if (messageAuthor.equals(author)) {
                        if (messageContent.contentEquals(rawMessage)) {
                            transliterated = true;
                            String title = Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.already-transliterated.title")));
                            String subtitle = Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.already-transliterated.subtitle")));
                            int time = VChat.instance.getConfig().getInt("chat.transliteration.already-transliterated.time") * 20;
                            author.sendTitle(title, subtitle, 0, time, 0);
                            author.playSound(author.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                        }
                    }
                }

                if (!transliterated) {
                    for (int i = 1; i < strings.length; i++) {
                        if (strings[i].startsWith(Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.symbol")))) {
                            transliteratedMessage.append(strings[i]);
                            if (i != strings.length - 1) {
                                transliteratedMessage.append(" ");
                            }
                            continue;
                        }
                        StringBuilder word = new StringBuilder();

                        for (char symbol : strings[i].toCharArray()) {
                            char russianSymbol = symbolMap.getOrDefault(symbol, symbol);
                            word.append(russianSymbol);
                        }

                        transliteratedMessage.append(word);
                        if (i != strings.length - 1) {
                            transliteratedMessage.append(" ");
                        }
                    }

                    transliteratedMessage.append(" ").append(Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.pattern"))));
                    MessageManager.instance.formatMessage(transliteratedMessage.toString(), (Player) commandSender, true, false);
                    ChatManager.instance.saveToTransliteratedMessages(author, rawMessage.toString());
                }
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
            subcommands.add("emojis");
            subcommands.add("whatsnew");
            if (commandSender.hasPermission("vchat.commands.reload")) subcommands.add("reload");
        }
        return subcommands;
    }
}

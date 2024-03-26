package by.vengdevs.vchat.managers;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.classes.Message;
import by.vengdevs.vchat.utils.Emoji;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    
    public static final MessageManager instance = new MessageManager();
    
    public void formatMessage(String message, Player author, boolean transliterated, boolean mentioning) {
        List<BaseComponent> messageComponents = new ArrayList<>();

        String message_pattern = VChat.instance.getConfig().getString("chat.pattern");
        assert message_pattern != null;
        String name = author.getCustomName() == null ? author.getName() : author.getCustomName();
        String answerHover = VChat.instance.getConfig().getString("chat.mentions.answer-hover");
        TextComponent whoSender = new TextComponent(ChatColor.translateAlternateColorCodes('&', message_pattern.replace("%NAME%", name)));
        whoSender.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(answerHover)));
        whoSender.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + author.getName() + " "));
        messageComponents.add(whoSender);

        List<Player> mentioned_players = new ArrayList<>();

        if (VChat.instance.getConfig().getBoolean("chat.emojis.enabled")) {
            String emojiSymbol = VChat.instance.getConfig().getString("chat.emojis.symbol");
            assert emojiSymbol != null;
            String regex = Pattern.quote(emojiSymbol) + "([^" + Pattern.quote(emojiSymbol) + "]+)" + Pattern.quote(emojiSymbol);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);

            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                String emojiName = matcher.group(1);
                String emoji = Emoji.getEmoji(emojiName);
                if (emoji != null) {
                    matcher.appendReplacement(result, emoji);
                } else {
                    matcher.appendReplacement(result, matcher.group());
                }
            }
            matcher.appendTail(result);
            message = result.toString();
        }

        String[] words = message.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (VChat.instance.getConfig().getBoolean("chat.links.enabled")) {
                if (words[i].startsWith("https://") && words[i].length() > 8) {
                    String pattern = VChat.instance.getConfig().getString("chat.links.https.pattern");
                    String hover = VChat.instance.getConfig().getString("chat.links.https.hover");
                    assert pattern != null;
                    assert hover != null;
                    TextComponent link = new TextComponent(ChatColor.translateAlternateColorCodes('&', pattern));
                    link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover.replace("%LINK%", words[i]))));
                    link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, words[i]));

                    messageComponents.add(link);
                    if (i != words.length - 1) {
                        messageComponents.add(new TextComponent(" "));
                    }
                    continue;
                } else if (words[i].startsWith("http://") && words[i].length() > 7) {
                    String pattern = VChat.instance.getConfig().getString("chat.links.http.pattern");
                    String hover = VChat.instance.getConfig().getString("chat.links.http.hover");
                    assert pattern != null;
                    assert hover != null;
                    TextComponent link = new TextComponent(ChatColor.translateAlternateColorCodes('&', pattern));
                    link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover.replace("%LINK%", words[i]))));
                    link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, words[i]));

                    messageComponents.add(link);
                    if (i != words.length - 1) {
                        messageComponents.add(new TextComponent(" "));
                    }
                    continue;
                }
            }
            if (VChat.instance.getConfig().getBoolean("chat.mentions.enabled")) {
                if (words[i].startsWith(Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.symbol"))) && words[i].length() > 1) {
                    TextComponent mention = new TextComponent(words[i]);
                    if (words[i].equalsIgnoreCase("@everyone") && VChat.instance.getConfig().getBoolean("chat.mentions.everyone.enabled")) {
                        for (Player object : Bukkit.getOnlinePlayers()) {
                            if (!mentioned_players.contains(object)) {
                                mentioned_players.add(object);
                            }
                        }
                        String pattern = VChat.instance.getConfig().getString("chat.mentions.everyone.pattern");
                        String hover = VChat.instance.getConfig().getString("chat.mentions.everyone.hover");
                        String author_name = author.getName();
                        assert pattern != null;
                        assert hover != null;
                        mention = new TextComponent(ChatColor.translateAlternateColorCodes('&', pattern));
                        mention.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover.replace("%AUTHOR%", author_name))));
                        mention.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@everyone"));
                    } else {
                        boolean player_exists = false;
                        Player mention_player = null;
                        for (Player object : Bukkit.getOnlinePlayers()) {
                            if (words[i].substring(1).equalsIgnoreCase(object.getName())) {
                                player_exists = true;
                                mention_player = object;
                                if (!mentioned_players.contains(object)) {
                                    mentioned_players.add(object);
                                }
                            }
                        }
                        if (player_exists) {
                            String pattern = VChat.instance.getConfig().getString("chat.mentions.pattern");
                            String hover = VChat.instance.getConfig().getString("chat.mentions.hover");
                            String author_name = author.getName();

                            assert pattern != null;
                            assert hover != null;
                            mention = new TextComponent(ChatColor.translateAlternateColorCodes('&', pattern.replace("%MENTION%", mention_player.getName())));
                            mention.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover.replace("%AUTHOR%", author_name).replace("%MENTION%", mention_player.getName()))));
                            mention.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + mention_player.getName()));
                        }
                    }

                    messageComponents.add(mention);
                    if (i != words.length - 1) {
                        messageComponents.add(new TextComponent(" "));
                    }
                    continue;
                }

            }

            messageComponents.add(new TextComponent(words[i]));
            if (i != words.length - 1) {
                messageComponents.add(new TextComponent(" "));
            }
        }

        ChatManager.instance.displayMessage(new Message(messageComponents.toArray(new BaseComponent[0]), author, mentioned_players, message, transliterated, mentioning));
    }

    private boolean possibleToTransliterate(Message message) {
        String startingChar = Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.symbol"));

        String regex = "(?<=^|\\s)" + Pattern.quote(startingChar) + "\\w+(?=\\s|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message.getRawContent());

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String matchedWord = matcher.group();
            boolean isPlayer = matchedWord.substring(1).equalsIgnoreCase("everyone");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(matchedWord.substring(1))) {
                    isPlayer = true;
                }
            }
            if (isPlayer) {
                boolean leftSpace = matcher.start() > 0 && message.getRawContent().charAt(matcher.start() - 1) == ' ';
                boolean rightSpace = matcher.end() < message.getRawContent().length() && message.getRawContent().charAt(matcher.end()) == ' ';
                if (leftSpace && rightSpace) matcher.appendReplacement(result, " ");
                else matcher.appendReplacement(result, "");
            } else matcher.appendTail(result);
        }
        matcher.appendTail(result);

        char[] englishSymbols = "qwertyuiop[]asdfghjkl;'zxcvbnm,.`QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>~".toCharArray();
        char[] russianSymbols = "йцукенгшщзхъфывапролджэячсмитьбюёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ".toCharArray();
        int englishCount = 0;
        int russianCount = 0;
        for (char symbol : result.toString().toCharArray()) {
            for (int i = 0; i < englishSymbols.length; i++) {
                if (englishSymbols[i] == symbol) englishCount++;
                if (russianSymbols[i] == symbol) russianCount++;
            }
        }
        return englishCount > russianCount;
    }

    public BaseComponent[] addTransliterateButton(Player player, Message message) {
        if (player.equals(message.getAuthor()) && VChat.instance.getConfig().getBoolean("chat.transliteration.enabled") && !message.getTransliterated() && possibleToTransliterate(message)) {
            List<BaseComponent> finalMessage = new ArrayList<>(List.of(message.getContent()));
            String buttonPattern = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.button-pattern")));
            String buttonHover = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.button-hover")));
            TextComponent transliterateComponent = new TextComponent(buttonPattern);
            transliterateComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(buttonHover)));
            transliterateComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chat transliterate " + message.getRawContent()));
            finalMessage.add(new TextComponent(" "));
            finalMessage.add(transliterateComponent);
            return finalMessage.toArray(new BaseComponent[0]);
        } else {
            return message.getContent();
        }
    }
}

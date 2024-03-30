package by.vengdevs.vchat.managers;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.classes.Message;
import by.vengdevs.vchat.utils.Color;
import by.vengdevs.vchat.utils.Emoji;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    public static final MessageManager instance = new MessageManager();

    public void formatMessage(String message, Player author, boolean transliterated, boolean mentioning) {
        if (isAnswer(message)) formatAnswer(message, author, transliterated, mentioning);
        else formatDefault(message, author, transliterated, mentioning);
    }

    private boolean isAnswer(String message) {
        String firstWord = message.split(" ")[0];
        return firstWord.startsWith("#") && firstWord.endsWith("#");
    }

    private void formatAnswer(String message, Player author, boolean transliterated, boolean mentioning) {
        int messageID;
        try {
            messageID = extractMessageID(message);
        } catch (NumberFormatException e) {
            formatDefault(message, author, transliterated, mentioning);
            return;
        }
        Message messageToAnswer = ChatManager.instance.getMessage(messageID);
        if (messageToAnswer == null) {
            formatDefault(message, author, transliterated, mentioning);
            return;
        }
        String trimmedMessage = message.substring(message.indexOf(' ') + 1);
        List<BaseComponent> basicComponents = formatBasicComponents(author);
        List<BaseComponent> messageComponents = formatMessageComponents(trimmedMessage);
        List<Player> mentionedPlayers = extractMentionedPlayers(message);
        List<BaseComponent> fullComponents = new ArrayList<>(basicComponents);
        fullComponents.addAll(messageComponents);

        if (VChat.instance.getConfig().getBoolean("chat.answers.enabled")) addAnswerActions(fullComponents, messageID);
        applyEmojis(fullComponents);
        Message finalMessage = new Message(fullComponents.toArray(new BaseComponent[0]), author, mentionedPlayers, trimmedMessage, transliterated, mentioning, messageID, messageToAnswer);
        ChatManager.instance.displayMessage(finalMessage);
        ChatManager.instance.setNewID(messageID + 1);
    }

    private void formatDefault(String message, Player author, boolean transliterated, boolean mentioning) {
        List<BaseComponent> basicComponents = formatBasicComponents(author);
        List<BaseComponent> messageComponents = formatMessageComponents(message);
        List<Player> mentionedPlayers = extractMentionedPlayers(message);
        int newID = ChatManager.instance.getNewID();
        if (VChat.instance.getConfig().getBoolean("chat.answers.enabled")) {
            addAnswerActions(messageComponents, newID);
        }
        List<BaseComponent> fullComponents = new ArrayList<>(basicComponents);
        fullComponents.addAll(messageComponents);
        applyEmojis(fullComponents);
        Message finalMessage = new Message(fullComponents.toArray(new BaseComponent[0]), author, mentionedPlayers, message, transliterated, mentioning, newID, null);
        ChatManager.instance.displayMessage(finalMessage);
        ChatManager.instance.setNewID(newID + 1);
    }

    private int extractMessageID(String message) {
        return Integer.parseInt(message.split(" ")[0].substring(1, message.split(" ")[0].length() - 1));
    }

    private List<BaseComponent> formatBasicComponents(Player author) {
        List<BaseComponent> basicComponents = new ArrayList<>();
        String messagePattern = Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString("chat.pattern")));
        String name = author.getCustomName() == null ? author.getName() : author.getCustomName();
        String mentionHover = VChat.instance.getConfig().getString("chat.mentions.mention-hover");
        TextComponent sender = new TextComponent(messagePattern.split("%NAME%")[0].split(" ")[messagePattern.split("%NAME%")[0].split(" ").length - 1] + name);
        sender.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(mentionHover)));
        sender.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "@" + author.getName() + " "));
        basicComponents.add(new TextComponent(messagePattern.split("%NAME%")[0]));
        basicComponents.add(sender);
        basicComponents.add(new TextComponent(messagePattern.split("%NAME%")[1]));
        return basicComponents;
    }

    private List<BaseComponent> formatMessageComponents(String message) {
        List<BaseComponent> messageComponents = new ArrayList<>();
        String[] words = message.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (VChat.instance.getConfig().getBoolean("chat.links.enabled")) {
                if (words[i].startsWith("https://") || words[i].startsWith("http://")) {
                    formatLinkComponent(words[i], messageComponents);
                    continue;
                }
            }
            if (VChat.instance.getConfig().getBoolean("chat.mentions.enabled")) {
                formatMentionComponent(words[i], messageComponents);
                continue;
            }
            messageComponents.add(new TextComponent(words[i]));
            if (i != words.length - 1) messageComponents.add(new TextComponent(" "));
        }
        return messageComponents;
    }

    private void formatLinkComponent(String word, List<BaseComponent> messageComponents) {
        String pattern = word.startsWith("https://") ?
                VChat.instance.getConfig().getString("chat.links.https.pattern") :
                VChat.instance.getConfig().getString("chat.links.http.pattern");
        String hover = word.startsWith("https://") ?
                VChat.instance.getConfig().getString("chat.links.https.hover") :
                VChat.instance.getConfig().getString("chat.links.http.hover");
        assert pattern != null;
        assert hover != null;
        TextComponent link = new TextComponent(Color.convert("&", pattern));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover.replace("%LINK%", word))));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, word));
        messageComponents.add(link);
        messageComponents.add(new TextComponent(" "));
    }

    private void formatMentionComponent(String word, List<BaseComponent> messageComponents) {
        String mentionSymbol = VChat.instance.getConfig().getString("chat.mentions.symbol");
        assert mentionSymbol != null;
        if (word.startsWith(mentionSymbol) && word.length() > mentionSymbol.length() + 1) {
            boolean isEveryoneMentionsEnabled = VChat.instance.getConfig().getBoolean("chat.mentions.everyone.enabled");
            if (word.equalsIgnoreCase(mentionSymbol + "everyone") && isEveryoneMentionsEnabled) messageComponents.add(formatMentionEveryoneComponent());
            else
                for (Player player : Bukkit.getOnlinePlayers())
                    if (player.getName().equalsIgnoreCase(word.substring(mentionSymbol.length())))
                        messageComponents.add(formatMentionPlayerComponent(player));
        } else messageComponents.add(new TextComponent(word));
        messageComponents.add(new TextComponent(" "));
    }

    private BaseComponent formatMentionEveryoneComponent() {
        String pattern = VChat.instance.getConfig().getString("chat.mentions.everyone.pattern");
        assert pattern != null;
        return new TextComponent(Color.convert("&", pattern));
    }

    private BaseComponent formatMentionPlayerComponent(Player player) {
        String pattern = VChat.instance.getConfig().getString("chat.mentions.pattern");
        assert pattern != null;
        return new TextComponent(Color.convert("&", pattern.replace("%MENTION%", player.getName())));
    }

    private List<Player> extractMentionedPlayers(String message) {
        List<Player> mentionedPlayers = new ArrayList<>();
        String mentionSymbol = VChat.instance.getConfig().getString("chat.mentions.symbol");
        if (mentionSymbol == null) return mentionedPlayers;
        for (String word : message.split(" ")) {
            if (word.startsWith(mentionSymbol)) {
                String playerName = word.substring(mentionSymbol.length());
                if (playerName.equalsIgnoreCase("everyone")) mentionedPlayers.addAll(Bukkit.getOnlinePlayers());
                else {
                    Player mentionedPlayer = Bukkit.getPlayer(playerName);
                    if (mentionedPlayer != null && !mentionedPlayers.contains(mentionedPlayer)) mentionedPlayers.add(mentionedPlayer);
                }
            }
        }
        return mentionedPlayers;
    }

    private void addAnswerActions(List<BaseComponent> messageComponents, int messageID) {
        for (BaseComponent component : messageComponents) {
            if (component.getClickEvent() == null && component.getHoverEvent() == null) {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(VChat.instance.getConfig().getString("chat.answers.hover"))));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "#" + messageID + "# "));
            }
        }
    }

    private void applyEmojis(List<BaseComponent> components) {
        if (VChat.instance.getConfig().getBoolean("chat.emojis.enabled")) {
            for (BaseComponent component : components) {
                if (component instanceof TextComponent textComponent) {
                    String emojiSymbol = VChat.instance.getConfig().getString("chat.emojis.symbol");
                    assert emojiSymbol != null;
                    String regex = Pattern.quote(emojiSymbol) + "([^" + Pattern.quote(emojiSymbol) + "]+)" + Pattern.quote(emojiSymbol);
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(textComponent.getText());
                    StringBuilder result = new StringBuilder();
                    while (matcher.find()) {
                        String emojiName = matcher.group(1);
                        String emoji = Emoji.getEmoji(emojiName);
                        if (emoji != null) matcher.appendReplacement(result, emoji);
                        else matcher.appendReplacement(result, matcher.group());
                    }
                    matcher.appendTail(result);
                    textComponent.setText(result.toString());
                }
            }
        }
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
            for (Player player : Bukkit.getOnlinePlayers()) if (player.getName().equalsIgnoreCase(matchedWord.substring(1))) isPlayer = true;
            if (isPlayer) {
                boolean leftSpace = matcher.start() > 0 && message.getRawContent().charAt(matcher.start() - 1) == ' ';
                boolean rightSpace = matcher.end() < message.getRawContent().length() && message.getRawContent().charAt(matcher.end()) == ' ';
                if (leftSpace && rightSpace) matcher.appendReplacement(result, " ");
                else matcher.appendReplacement(result, "");
            } else matcher.appendTail(result);
        }
        matcher.appendTail(result);
        StringBuilder messageBuilder = new StringBuilder();
        String[] words = result.toString().split(" ");
        for (int i = 0; i < words.length; i++) {
            if (!words[i].startsWith("https://") && !words[i].startsWith("http://")) messageBuilder.append(words[i]);
            if (i != words.length - 1) messageBuilder.append(" ");
        }
        char[] englishSymbols = "qwertyuiop[]asdfghjkl;'zxcvbnm,.`QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>~".toCharArray();
        char[] russianSymbols = "йцукенгшщзхъфывапролджэячсмитьбюёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ".toCharArray();
        int englishCount = 0;
        int russianCount = 0;
        for (char symbol : messageBuilder.toString().toCharArray()) {
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
            String buttonPattern = Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.button-pattern")));
            String buttonHover = Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.button-hover")));
            TextComponent transliterateComponent = new TextComponent(buttonPattern);
            transliterateComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(buttonHover)));
            transliterateComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chat transliterate " + message.getRawContent()));
            finalMessage.add(transliterateComponent);
            return finalMessage.toArray(new BaseComponent[0]);
        } else return message.getContent();
    }
}

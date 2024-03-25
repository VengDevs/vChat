package by.vengdevs.vchat.utils;

import by.vengdevs.vchat.VChat;
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

public class MessageManager {
    
    public static final MessageManager instance = new MessageManager();
    
    public void formatMessage(String message, Player author, boolean transliterated) {
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
                if (words[i].startsWith("@") && words[i].length() > 1) {
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

        ChatManager.instance.displayMessage(new Message(messageComponents.toArray(new BaseComponent[0]), author, mentioned_players, message, transliterated));
    }
}

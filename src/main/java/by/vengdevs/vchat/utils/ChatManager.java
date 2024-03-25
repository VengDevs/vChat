package by.vengdevs.vchat.utils;

import by.vengdevs.vchat.VChat;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private static final List<Message> chatHistory = new ArrayList<>();

    public void displayMessage(Message message) {
        String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.title.title")));
        String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.title.subtitle")));
        int time = VChat.instance.getConfig().getInt("chat.mentions.title.time") * 20;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(message.getAuthor()) && VChat.instance.getConfig().getBoolean("chat.transliteration.enabled") && !message.getTransliterated()) {
                List<BaseComponent> finalMessage = new ArrayList<>(List.of(message.getContent()));
                String buttonPattern = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.button-pattern")));
                String buttonHover = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.transliteration.button-hover")));
                TextComponent transliterateComponent = new TextComponent(buttonPattern);
                transliterateComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(buttonHover)));
                transliterateComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chat transliterate " + message.getRawContent()));
                finalMessage.add(new TextComponent(" "));
                finalMessage.add(transliterateComponent);
                player.spigot().sendMessage(finalMessage.toArray(new BaseComponent[0]));
            } else {
                player.spigot().sendMessage(message.getContent());
            }
            if (message.getMentions().contains(player)) {
                player.sendTitle(title.replace("%AUTHOR%", message.getAuthor().getName()), subtitle.replace("%AUTHOR%", message.getAuthor().getName()), 0, time, 0);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
            }
        }

        saveMessageToChatHistory(message);
        Logger.instance.sendConsole(message.getAuthor().getName() + " > " + message.getRawContent());
    }

    private void saveMessageToChatHistory(Message message) {
        chatHistory.add(message);
    }

    public void displayChatHistory(Player player) {
        for (Message message : chatHistory) {
            player.spigot().sendMessage(message.getContent());
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.chat-history.message"))));
    }
}
